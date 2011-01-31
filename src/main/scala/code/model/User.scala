

package code.model 

import net.liftweb._
import mapper._
import util._
import common._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.util.BindHelpers._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
             <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password, textArea)

  // comment this line out to require email validations
  override def skipEmailValidation = true

  /**
   * custom method which doesn't trash the session.
   */
  override def logUserIn(who: TheUserType, postLogin: () => Nothing): Nothing = {
    logUserIn(who)
    postLogin()
  }

  override def login = {
    for {r <- S.request
         ret <- r.param("returnTo")}
    loginRedirect(Full(Helpers.urlDecode(ret)))
    super.login
  }
  
/*
  override def login = { 
    if (S.post_?) {
      S.param("username").
      flatMap(username => findUserByUserName(username)) match {
        case Full(user) if user.validated_? &&
          user.testPassword(S.param("password")) => {
            logUserIn(user, () => {
              S.notice(S.??("logged.in"))

              val redir = loginRedirect.is match {
                case Full(url) =>
                  loginRedirect(Empty)
                url 
                case _ =>
                  homePage
              }   

              val ret = S.param("returnTo") openOr "/sorry-the-session-was-trashed"

              S.redirectTo(ret)
            })  
          }   

        case Full(user) if !user.validated_? =>
          S.error(S.??("account.validation.error"))

        case _ => S.error(S.??("invalid.credentials"))
      }   
    }   

    bind("user", loginXhtml,
         "email" -> (FocusOnLoad(<input type="text" name="username"/>)),
         "password" -> (<input type="password" name="password"/>),
         "submit" -> (<input type="submit" value={S.??("log.in")}/>))
  }
  */
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server

  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
}


