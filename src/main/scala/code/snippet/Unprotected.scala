


package code.snippet

import xml.{NodeSeq}
import net.liftweb._
import util.Helpers._
import net.liftweb.http._

class Unprotected {
  def render = { 
    <form method="post" action="protected">
      <input type="text" name="someVal" value="123"/>
      <input type="submit" value="Go"/>
    </form>
  }
}
