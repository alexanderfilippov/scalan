package scalan
package it.lms

import scalan.compilation.lms._
import scalan.it.smoke.SmokeItTests

class LmsSmokeItTests extends SmokeItTests {
  class ProgExp extends Prog with ScalanCtxExp with LmsCompiler { self =>
    def makeBridge[A, B] = new CoreBridge[A, B] {
      val scalan = self
      val lms = new CoreLmsBackend
    }
  }
  
  override val progStaged = new ProgExp

  test("test0simpleArith") {
    val in = 2
    compareOutputWithSequential(progStaged)(progSeq.simpleArith, progStaged.simpleArith, "simpleArith", in)
  }
  test("test1simpleArrGet") {
    val in = (Array(2,3), 1)
    compareOutputWithSequential(progStaged)(progSeq.simpleArrGet, progStaged.simpleArrGet, "simpleArrGet", in)
  }
  test("test2simpleMap") {
    val in = Array(2,3)
    compareOutputWithSequential(progStaged)(progSeq.simpleMap, progStaged.simpleMap, "simpleMap", in)
  }
  test("test3simpleMapNested") {
    val in = (Array(Array(2.0,3.0), Array(3.0,4.0)), 1)
    compareOutputWithSequential(progStaged)(progSeq.simpleMapNested, progStaged.simpleMapNested, "simpleMapNested", in)
  }
  test("test4simpleZip") {
    val in = Array(2,3)
    compareOutputWithSequential(progStaged)(progSeq.simpleZip, progStaged.simpleZip, "simpleZip", in)
  }
  test("test5simpleZipWith") {
    val in = Array(2,3)
    compareOutputWithSequential(progStaged)(progSeq.simpleZipWith, progStaged.simpleZipWith, "simpleZipWith", in)
  }
  test("test6simpleReduce") {
    val in = Array(2,3)
    compareOutputWithSequential(progStaged)(progSeq.simpleReduce, progStaged.simpleReduce, "simpleArith", in)
  }
  test("test7mvMul") {
    val in = (Array(Array(2,3), Array(4,5)), Array(6,7))
    compareOutputWithSequential(progStaged)(progSeq.mvMul, progStaged.mvMul, "mvMul", in)
  }
  test("test9simpleIf") {
    val in = (Array(2.0,3.0), 4.0)
    compareOutputWithSequential(progStaged)(progSeq.simpleIf, progStaged.simpleIf, "simpleIf", in)
  }
  test("test10simpleSum") {
    val in = 7
    compareOutputWithSequential(progStaged)(progSeq.simpleSum, progStaged.simpleSum, "simpleSum", in)
  }
  test("test11simpleOptionOps") {
    val in = 7
    compareOutputWithSequential(progStaged)(progSeq.simpleOptionOps, progStaged.simpleOptionOps, "simpleOptionOps", in)
  }

}
