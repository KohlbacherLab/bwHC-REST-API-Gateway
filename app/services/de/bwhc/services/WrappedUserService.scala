package de.bwhc.services


import javax.inject.Singleton


import de.bwhc.rest.util.ServiceWrapper

import de.bwhc.user.api.UserService


@Singleton
class WrappedUserService extends ServiceWrapper(UserService)

