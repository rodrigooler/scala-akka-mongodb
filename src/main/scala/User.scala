import org.bson.types.ObjectId
import org.mongodb.scala.bson.ObjectId

case class FindByIdRequest(id: String) {
  require(ObjectId.isValid(id), "the informed id is not a representation of a valid hex string")
}

case class UserResource(id: String, username: String, age: Int) {
  require(username != null, "username not informed")
  require(username.nonEmpty, "username cannot be empty")
  require(age > 0, "age cannot be lower than 1")

  def asDomain = User(if (id == null) ObjectId.get() else new ObjectId(id), username, age)
}

case class User(_id: ObjectId, username: String, age: Int) {
  def asResource = UserResource(_id.toHexString, username, age)
}
