package com.example.pingping_android

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import splitties.toast.toast
import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.net.SocketException

class SocketTask{
    lateinit var socket: Socket
    lateinit var address: InetAddress

    fun connect(ip: String, main: MainActivity){
        GlobalScope.launch(Dispatchers.Main) {
            var b = true

            withContext(Dispatchers.IO){
                try {
                    address = InetAddress.getByName(ip)
                    socket = Socket(address, 7777)
                }catch (e: IOException){
                    e.printStackTrace()
                    b = false
                }
            }

            main.update(b, ip)
        }
    }

    fun sendMsg(cmd: String){
        GlobalScope.launch(Dispatchers.IO) {
            if(socket.isClosed)
                socket = Socket(address, 7777)

            try{
                send(cmd, socket)
            }catch (e: SocketException){
                send(cmd, Socket(address, 7777))
            }

        }
    }

    private fun send(cmd: String, socket: Socket) {
        PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream()) as Writer), true).use {
            it.write(cmd)
        }
    }
}