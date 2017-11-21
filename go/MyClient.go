package main

import (
	"net"
	"net/http"
	"./model"
	"strings"
	"fmt"
	"io/ioutil"
	"encoding/json"
	"bytes"
	"bufio"
	"os"
)
var logs = ""
var messageport uint16
var myusername string

type Client struct {
	User       *model.User
	HTTPClient *http.Client
	Conn       *net.Conn
}

func (client *Client) joinRequest(roomNum int) {
	// Format the body for serialization
	joinRequest := &client.User
	b := new(bytes.Buffer)
	json.NewEncoder(b).Encode(&joinRequest)

	resp, _ := client.HTTPClient.Post("http://localhost:8000/chatroom/join",
		"application/json; charset=utf-8",
			b)
	defer resp.Body.Close()
}

func formatMessage(roomNum int, username string, body string) *model.Message {
	return &model.Message{ChatRoom: roomNum, Username: username, Body: strings.TrimSpace(body)}
}

func (client *Client) sendMessage(roomNum int, msg string) {
	m := formatMessage(roomNum, client.User.Username, msg)
	bufmsg := model.ConvertMessageToBuffer(m)
	response, err := client.HTTPClient.Post("http://localhost:8000/message",
		"application/json; charset=utf-8",
			bufmsg)
	defer response.Body.Close()
	if err != nil {
		fmt.Println(err.Error())
		return
	}
}


func receiveMessage(writer http.ResponseWriter, req *http.Request) {
	bodyBytes, err := ioutil.ReadAll(req.Body)
	if err != nil {
		fmt.Println("Error reading the message from the request body")
		writer.WriteHeader(http.StatusBadRequest)
	}
	var message *model.Message
	json.Unmarshal(bodyBytes, &message)

	logs += message.ReadableFormat()
	fmt.Println(message.ReadableFormat())
}



func (client *Client) subscribeToServer() {
	fmt.Println("Starting client message subscription...")
	http.HandleFunc("/message", receiveMessage)
	fmt.Println((http.ListenAndServe(fmt.Sprintf(":%d", messageport), nil).Error()))
}

// Create makes a new tcp client and waits to send a message to the target server.
func Create() {
	fmt.Println("Creating client...")
	// Create the client
	client := &Client{
		HTTPClient: &http.Client{},
		User:       &model.User{Username:myusername,RoomIndex:0,MessagePort:messageport},
	}// TODO: replace with different user stuff
	go client.subscribeToServer()

	// send message to server
	client.joinRequest(0)

	fmt.Println("Reading client...")

	buf := bufio.NewReader(os.Stdin)


	var msg = ""
	for msg!="/exit"{
		msg,_ = buf.ReadString('\n')

		client.sendMessage(0,msg)
	}
}

func main(){
	myusername="test"
	messageport=8080
	fmt.Print("Enter username: ")
	fmt.Scanf("%s\n",&myusername)
	fmt.Print("Enter port number: ")
	fmt.Scanf("%d\n",&messageport)
	Create()
}
