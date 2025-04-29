package util

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class commandspec extends AnyWordSpec with Matchers {

  // Mock-Implementierung für den Command-Trait
  class TestCommand() extends Command {
    var doStepCalled = false
    var undoStepCalled = false

    override def doStep: Unit = { doStepCalled = true }
    override def undoStep: Unit = { undoStepCalled = true }
  }

  // Tests für den UndoManager
  "UndoManager" should {
    "correctly manage doStep and undoStep of a Command" in {
      val command = new TestCommand()
      val undoManager = new UndoManager()

      // Test doStep
      undoManager.doStep(command)
      command.doStepCalled should be(true)

      // Test undoStep
      undoManager.undoStep
      command.undoStepCalled should be(true)
    }
  }
}
