

package bootstrap.liftweb

import net.liftweb._
import http._
import sitemap.{SiteMap, Menu, Loc}
import util._
import net.liftweb._
import mapper.{Schemifier, DB, StandardDBVendor, DefaultConnectionIdentifier}
import util.{Props}
import common.{Full}
import http.{S}
import code.model._



class Boot {
  def boot {
  
    if (!DB.jndiJdbcConnAvailable_?) {
       val vendor = 
         new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
         			               Props.get("db.url") openOr 
         			               "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
         			               Props.get("db.user"), Props.get("db.password"))
 
       LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
 
       DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
     }
  
  
    // where to search snippet
    LiftRules.addToPackages("code")

    val RequireLogin = Loc.EarlyResponse(() => Full(
        RedirectResponse("/user_mgt/login?returnTo="+Helpers.urlEncode(S.uriAndQueryString.open_!))).filter(ignore => !User.loggedIn_?))

    // build sitemap
    val entries = List(Menu("Home") / "index",
      Menu("Unprotected") / "unprotected",
      Menu("Protected") / "protected" >> RequireLogin) :::
      User.sitemap
    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    Schemifier.schemify(true, Schemifier.infoF _, User)

    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    S.addAround(DB.buildLoanWrapper) 
  }
}
