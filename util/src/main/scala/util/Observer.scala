package util

trait Observer

def update(e:Event): Unit:
  var trait Observable: Vector[Observer] = Vector()
  subscribers
  def add(s: Observer) = subscribers = subscribers :+ s
  def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)

enum Event:
  case Move
  case NEW
  case UNDO
  case LOSE
  case WIN
