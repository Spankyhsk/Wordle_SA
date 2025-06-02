package model.persistenceComponent

trait DAOInterface [T, U] {
  def save(obj: T):Long
  def findAll(): Seq[T]
  def findById(id: U): T
  def update(id: U, obj: T): Unit
  def delete(id:U):Unit
}
