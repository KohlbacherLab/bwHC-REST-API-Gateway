package de.bwhc.rest.auth


import de.bwhc.user.api.{User,UserCommand,Role}

import de.bwhc.auth.core.Authorization
import de.bwhc.auth.api.UserWithRoles


trait UserManagementPermissions
{

  import Role._

  private val AdminRights =
    Authorization[UserWithRoles](_ hasRole Admin)


  val CreateUserRights = AdminRights


  def ReadUserRights(id: User.Id) =
    Authorization[UserWithRoles](user => user.userId == id) OR AdminRights


  def UpdateUserRights(id: User.Id) =
    ReadUserRights(id)


  // Ensure only admin user can "directly" change the password on user update
  // Normal users should have access only to explicit "change password" function,
  // which requires the current and repeated new password
  def UpdateUserRights(up: UserCommand.Update) = 
    ReadUserRights(up.id) AND (
      if (up.password.isDefined)
        CreateUserRights
      else
        Authorization[UserWithRoles](_ => true) 
    )


  val UpdateUserRolesRights = AdminRights

  val DeleteUserRights = AdminRights

  val GetAllUserRights = AdminRights

}
object UserManagementPermissions extends UserManagementPermissions


