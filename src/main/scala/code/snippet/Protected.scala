


package code.snippet

import xml.{NodeSeq}
import net.liftweb._
import util.Helpers._
import net.liftweb.http._

class Protected {
  def render = {
    val someVal = S.param("someVal") openOr {S.warning("Please enter a value."); S.redirectTo(S.referer openOr "/")}
    <span>You entered: {someVal}</span>
  }
}
