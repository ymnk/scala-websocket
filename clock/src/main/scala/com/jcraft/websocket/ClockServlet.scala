package com.jcraft.websocket

import scala.collection.mutable.Set
import scala.concurrent.ops.spawn
import javax.servlet.http._
import org.eclipse.jetty.websocket._
import org.eclipse.jetty.websocket.WebSocket.Outbound

class ClockServlet extends WebSocketServlet {
  val clients = Set.empty[ClockWebSocket]

  spawn{
    val frame:Byte = 0
    while(true) {
      val time:String = (new java.util.Date).toString
      clients.foreach{ c => c.outbound.sendMessage(frame, time) }
      try{ Thread.sleep(1000) } catch { case e => }
    }
  }

  override def doGet(req:HttpServletRequest, res:HttpServletResponse ) =
    getServletContext.getNamedDispatcher("default").forward(req, res)

  override def doWebSocketConnect(req:HttpServletRequest, protocol:String ) =
    new ClockWebSocket

  class ClockWebSocket extends WebSocket {

    var outbound:Outbound = _

    override def onConnect(outbound:Outbound ) = {
      this.outbound = outbound
      clients += this
    }

    override def onMessage(frame:Byte, data:Array[Byte], offset:Int, length:Int ) = {}

    override def onMessage(frame:Byte, data:String ) = { }

    override def onDisconnect = {
      clients -= this
    }

  }
}

