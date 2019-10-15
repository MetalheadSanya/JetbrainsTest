package ru.zalutskii.google.test.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.kotlintest.TestCase
import io.kotlintest.specs.BehaviorSpec
import org.mockito.internal.util.reflection.FieldSetter
import ru.zalutskii.google.test.parser.list.TestListParser
import ru.zalutskii.google.test.parser.list.TestTree
import ru.zalutskii.google.test.service.log.performer.LogPerformerInput
import ru.zalutskii.google.test.service.process.TestProcess
import java.io.BufferedReader
import java.io.File
import java.io.StringReader

class TestServiceSpec : BehaviorSpec() {
    private val readyFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.READY)
    private val readyFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.READY)
    private val readyFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.READY)
    private val readyFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.READY)
    private val readySuite1 = TestTree.Case("Suite1", null, TestTree.Status.READY, listOf(readyFunc11, readyFunc12))
    private val readySuite2 = TestTree.Case("Suite2", null, TestTree.Status.READY, listOf(readyFunc21, readyFunc22))
    private val readyTree = TestTree(listOf(readySuite1, readySuite2))

    private val queuedFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.QUEUE)
    private val queuedFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.QUEUE)
    private val queuedFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
    private val queuedFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
    private val queuedSuite1 = TestTree.Case("Suite1", null, TestTree.Status.QUEUE, listOf(queuedFunc11, queuedFunc12))
    private val queuedSuite2 = TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(queuedFunc21, queuedFunc22))
    private val queuedTree = TestTree(listOf(queuedSuite1, queuedSuite2), TestTree.Status.QUEUE)

    private val successFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.SUCCESS)
    private val successFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.SUCCESS)
    private val failedFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.FAIL)
    private val successFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.SUCCESS)
    private val successSuite1 =
        TestTree.Case("Suite1", null, TestTree.Status.SUCCESS, listOf(successFunc11, successFunc12))
    private val failedSuite2 = TestTree.Case("Suite2", null, TestTree.Status.FAIL, listOf(failedFunc21, successFunc22))
    private val failedTree = TestTree(listOf(successSuite1, failedSuite2), TestTree.Status.FAIL)

    private val testToRun = "Suite2.Func21"

    private val successFunc21 = TestTree.Case.Function("Func22", null, TestTree.Status.SUCCESS)
    private val successSuite2 =
        TestTree.Case("Suite2", null, TestTree.Status.SUCCESS, listOf(successFunc21, successFunc22))


    private val runFailedSuite2 =
        TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(queuedFunc21, successFunc22))
    private val runFailedTree = TestTree(listOf(successSuite1, runFailedSuite2), TestTree.Status.QUEUE)

    private val buffer = BufferedReader(StringReader(""))
    private val file = File("test.txt")

    // Mocks

    private lateinit var parserMock: TestListParser
    private lateinit var processMock: TestProcess
    private lateinit var outputMock: TestServiceOutput
    private lateinit var logPerformerMock: LogPerformerInput


    private lateinit var service: TestService

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

        parserMock = mock {
            on { parseTree(any()) }.thenReturn(readyTree)
        }

        processMock = mock {
            on { readTestCases() }.thenReturn(buffer)
            on { runTestCases() }.thenReturn(buffer)
            on { runTests(any()) }.thenReturn(buffer)
        }

        outputMock = mock()

        logPerformerMock = mock {
            onBlocking { setCurrentLogTo(any(), any()) }.thenReturn("suite.test")
            onBlocking { setCurrentLogTo(any()) }.thenReturn("suite")
            onBlocking { setCurrentLogToRoot() }.thenReturn("Full log")
        }

        service = TestService(parserMock, processMock)
        service.output = outputMock
        service.logPerformer = logPerformerMock
    }

    init {
        given("service") {
            `when`("open called") {
                then("log performer must be reset") {
                    service.open(file)
                    verify(logPerformerMock).reset()
                }
                then("test tree must be updated") {
                    service.open(file)
                    verify(outputMock).didLoadTestTree(readyTree)
                }
                then("test log must be reset") {
                    service.open(file)
                    verify(outputMock).didProcessOutput("")
                }
            }

            `when`("run called") {
                then("log performer must be reset") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), readyTree)
                    service.run()
                    verify(logPerformerMock).reset()
                }
                then("test log must be reset") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), readyTree)
                    service.run()
                    verify(outputMock).didProcessOutput("")
                }
                then("tree status must be queued") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), readyTree)
                    service.run()
                    verify(outputMock).didUpdateTestTree(queuedTree)
                }
                then("test must be run") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), readyTree)
                    service.run()
                    verify(processMock).runTestCases()
                }
                then("log performer must start process log") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), readyTree)
                    service.run()
                    verify(logPerformerMock).perform(buffer)
                }
            }

            `when`("stop called") {
                then("process must stop") {
                    val startFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.SUCCESS)
                    val startFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.FAIL)
                    val startFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                    val startFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.RUN)
                    val startSuite1 =
                        TestTree.Case("Suite1", "200", TestTree.Status.FAIL, listOf(startFunc11, startFunc12))
                    val startSuite2 =
                        TestTree.Case("Suite2", null, TestTree.Status.RUN, listOf(startFunc21, startFunc22))
                    val startTree = TestTree(listOf(startSuite1, startSuite2))

                    val resultFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.SUCCESS)
                    val resultFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.FAIL)
                    val resultFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.READY)
                    val resultFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.READY)
                    val resultSuite1 =
                        TestTree.Case("Suite1", "200", TestTree.Status.FAIL, listOf(resultFunc11, resultFunc12))
                    val resultSuite2 =
                        TestTree.Case("Suite2", null, TestTree.Status.READY, listOf(resultFunc21, resultFunc22))
                    val resultTree = TestTree(listOf(resultSuite1, resultSuite2), TestTree.Status.READY)

                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), startTree)

                    service.stop()
                    verify(processMock).stop()
                    verify(outputMock).didUpdateTestTree(resultTree)
                }
            }

            `when`("rerun failed tests called") {
                then("log performer must be reset") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), failedTree)
                    service.rerunFailedTests()
                    verify(logPerformerMock).reset()
                }
                then("test log must be reset") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), failedTree)
                    service.rerunFailedTests()
                    verify(outputMock).didProcessOutput("")
                }
                then("tree status must be queued") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), failedTree)
                    service.rerunFailedTests()
                    verify(outputMock).didUpdateTestTree(runFailedTree)
                }
                then("test must be run") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), failedTree)
                    service.rerunFailedTests()
                    verify(processMock).runTests(listOf(testToRun))
                }
                then("log performer must start process log") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), failedTree)
                    service.rerunFailedTests()
                    verify(logPerformerMock).perform(buffer)
                }
            }

            `when`("show log for test called") {
                then("return new log to output") {
                    service.changeLogTo("suite", "test")
                    verify(outputMock).didProcessOutput("suite.test")
                }
            }

            `when`("show log for suite called") {
                then("return new log to output") {
                    service.changeLogTo("suite")
                    verify(outputMock).didProcessOutput("suite")
                }
            }

            `when`("show full log called") {
                then("return new log to output") {
                    service.changeLogTo()
                    verify(outputMock).didProcessOutput("Full log")
                }
            }

            `when`("did process called") {
                then("return new log to output") {
                    service.didProcessLog("test")
                    verify(outputMock).didProcessOutput("test")
                }
            }

            `when`("did start test suite called") {
                then("suite must be started") {
                    val resultFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.QUEUE)
                    val resultFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.QUEUE)
                    val resultFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                    val resultFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
                    val resultSuite1 =
                        TestTree.Case("Suite1", null, TestTree.Status.RUN, listOf(resultFunc11, resultFunc12))
                    val resultSuite2 =
                        TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(resultFunc21, resultFunc22))
                    val resultTree = TestTree(listOf(resultSuite1, resultSuite2), TestTree.Status.QUEUE)

                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), queuedTree)
                    service.didStartTestSuite("Suite1")
                    verify(outputMock).didUpdateTestTree(resultTree)
                }
            }

            `when`("did end test suite called") {
                and("all func success") {
                    then("suite must be success") {
                        val startFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.SUCCESS)
                        val startFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.SUCCESS)
                        val startFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                        val startFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
                        val startSuite1 =
                            TestTree.Case("Suite1", null, TestTree.Status.RUN, listOf(startFunc11, startFunc12))
                        val startSuite2 =
                            TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(startFunc21, startFunc22))
                        val startTree = TestTree(listOf(startSuite1, startSuite2))

                        val resultFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.SUCCESS)
                        val resultFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.SUCCESS)
                        val resultFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                        val resultFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
                        val resultSuite1 =
                            TestTree.Case("Suite1", "200", TestTree.Status.SUCCESS, listOf(resultFunc11, resultFunc12))
                        val resultSuite2 =
                            TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(resultFunc21, resultFunc22))
                        val resultTree = TestTree(listOf(resultSuite1, resultSuite2))

                        FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), startTree)
                        service.didEndTestSuite("Suite1", 200)
                        verify(outputMock).didUpdateTestTree(resultTree)
                    }
                }

                and("any of func fail") {
                    then("suite must be fail") {
                        val startFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.SUCCESS)
                        val startFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.FAIL)
                        val startFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                        val startFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
                        val startSuite1 =
                            TestTree.Case("Suite1", null, TestTree.Status.RUN, listOf(startFunc11, startFunc12))
                        val startSuite2 =
                            TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(startFunc21, startFunc22))
                        val startTree = TestTree(listOf(startSuite1, startSuite2))

                        val resultFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.SUCCESS)
                        val resultFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.FAIL)
                        val resultFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                        val resultFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
                        val resultSuite1 =
                            TestTree.Case("Suite1", "200", TestTree.Status.FAIL, listOf(resultFunc11, resultFunc12))
                        val resultSuite2 =
                            TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(resultFunc21, resultFunc22))
                        val resultTree = TestTree(listOf(resultSuite1, resultSuite2))

                        FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), startTree)
                        service.didEndTestSuite("Suite1", 200)
                        verify(outputMock).didUpdateTestTree(resultTree)
                    }
                }
            }

            `when`("did start test called") {
                then("test must be run") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), queuedTree)
                    service.didStartTest("Suite1", "Func11")

                    val resultFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.RUN)
                    val resultFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.QUEUE)
                    val resultFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                    val resultFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
                    val resultSuite1 =
                        TestTree.Case("Suite1", null, TestTree.Status.QUEUE, listOf(resultFunc11, resultFunc12))
                    val resultSuite2 =
                        TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(resultFunc21, resultFunc22))
                    val resultTree = TestTree(listOf(resultSuite1, resultSuite2), TestTree.Status.QUEUE)
                    verify(outputMock).didUpdateTestTree(resultTree)
                }
            }

            `when`("did pass test called") {
                then("test must be success") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), queuedTree)
                    service.didPassTest("Suite1", "Func11", 200)

                    val resultFunc11 = TestTree.Case.Function("Func11", "200", TestTree.Status.SUCCESS)
                    val resultFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.QUEUE)
                    val resultFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                    val resultFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
                    val resultSuite1 =
                        TestTree.Case("Suite1", null, TestTree.Status.QUEUE, listOf(resultFunc11, resultFunc12))
                    val resultSuite2 =
                        TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(resultFunc21, resultFunc22))
                    val resultTree = TestTree(listOf(resultSuite1, resultSuite2), TestTree.Status.QUEUE)
                    verify(outputMock).didUpdateTestTree(resultTree)
                }
            }

            `when`("did fail test called") {
                then("test must be fail") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), queuedTree)
                    service.didFailTest("Suite1", "Func11", 200)

                    val resultFunc11 = TestTree.Case.Function("Func11", "200", TestTree.Status.FAIL)
                    val resultFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.QUEUE)
                    val resultFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                    val resultFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
                    val resultSuite1 =
                        TestTree.Case("Suite1", null, TestTree.Status.QUEUE, listOf(resultFunc11, resultFunc12))
                    val resultSuite2 =
                        TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(resultFunc21, resultFunc22))
                    val resultTree = TestTree(listOf(resultSuite1, resultSuite2), TestTree.Status.QUEUE)
                    verify(outputMock).didUpdateTestTree(resultTree)
                }
            }

            `when`("did start root test called") {
                then("root node must be run") {
                    FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), queuedTree)
                    service.didStartTest()

                    val resultFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.QUEUE)
                    val resultFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.QUEUE)
                    val resultFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.QUEUE)
                    val resultFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.QUEUE)
                    val resultSuite1 =
                        TestTree.Case("Suite1", null, TestTree.Status.QUEUE, listOf(resultFunc11, resultFunc12))
                    val resultSuite2 =
                        TestTree.Case("Suite2", null, TestTree.Status.QUEUE, listOf(resultFunc21, resultFunc22))
                    val resultTree = TestTree(listOf(resultSuite1, resultSuite2), TestTree.Status.RUN)
                    verify(outputMock).didUpdateTestTree(resultTree)
                }
            }

            `when`("did end root test called") {
                and("any suite fail") {
                    then("root node must be failed") {
                        val startFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.FAIL)
                        val startFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.SUCCESS)
                        val startFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.SUCCESS)
                        val startFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.SUCCESS)
                        val startSuite1 =
                            TestTree.Case("Suite1", null, TestTree.Status.FAIL, listOf(startFunc11, startFunc12))
                        val startSuite2 =
                            TestTree.Case("Suite2", null, TestTree.Status.SUCCESS, listOf(startFunc21, startFunc22))
                        val startTree = TestTree(listOf(startSuite1, startSuite2), TestTree.Status.RUN)

                        FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), startTree)
                        service.didEndTest()

                        val resultTree = TestTree(listOf(startSuite1, startSuite2), TestTree.Status.FAIL)
                        verify(outputMock).didUpdateTestTree(resultTree)
                    }
                }
                and("all suite success") {
                    then("root node must be success") {
                        val startFunc11 = TestTree.Case.Function("Func11", null, TestTree.Status.SUCCESS)
                        val startFunc12 = TestTree.Case.Function("Func12", null, TestTree.Status.SUCCESS)
                        val startFunc21 = TestTree.Case.Function("Func21", null, TestTree.Status.SUCCESS)
                        val startFunc22 = TestTree.Case.Function("Func22", null, TestTree.Status.SUCCESS)
                        val startSuite1 =
                            TestTree.Case("Suite1", null, TestTree.Status.SUCCESS, listOf(startFunc11, startFunc12))
                        val startSuite2 =
                            TestTree.Case("Suite2", null, TestTree.Status.SUCCESS, listOf(startFunc21, startFunc22))
                        val startTree = TestTree(listOf(startSuite1, startSuite2), TestTree.Status.RUN)

                        FieldSetter.setField(service, service.javaClass.getDeclaredField("tree"), startTree)
                        service.didEndTest()

                        val resultTree = TestTree(listOf(startSuite1, startSuite2), TestTree.Status.SUCCESS)
                        verify(outputMock).didUpdateTestTree(resultTree)
                    }
                }
            }
        }
    }
}