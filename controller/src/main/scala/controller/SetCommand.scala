package controller

import util.Command

class SetCommand( key:Int, feedback: Map[Int, String], controller:controll)extends Command{

  override def doStep: Unit = {controller.step(key, feedback)}

  override def undoStep: Unit = {controller.undoStep(key, feedback)}
  
}
