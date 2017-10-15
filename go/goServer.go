package main

import (
	//"fmt"
	"bufio"
	"net"
	"os/exec"
	"fmt"
)


type Client struct{
	incoming chan string
	outgoing chan string
	reader *bufio.Reader
	writer *bufio.Writer
}






type ChatRoom struct{
	clients []*Client
	joins chan net.Conn
	incoming chan string
	outgoing chan string
}

func(room *ChatRoom) Broadcast(){

}

func(room *ChatRoom) Join(){

}

func(room *ChatRoom) Listen(){

}

func(room *ChatRoom) NewChatRoom(){

}




func main() {

	fmt.Println("Hello")
	var lol = ""
	fmt.Scanln(&lol)
	//chat := NewChatRoom
	if lol == "start"{
		cmd:= exec.Command("cmd","/C","start","go run C:/Users/aj3x/OneDrive/Documents/cmpt/436/Asn1/go/goServer.go")
		_ = cmd.Start()
	}
}
