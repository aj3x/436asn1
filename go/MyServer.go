package main

import(
	"./model"
	"fmt"
	"net/http"
	"encoding/json"
	"io/ioutil"
)

var chatrooms =make([]room,10)
var clientlist = make(map[string] model.User)

//const (
//	Address = ""//string
//	Port = 8000//uint16
//)

type room struct{
	log string
}


func joinRoom(writer http.ResponseWriter, req *http.Request) {
	bodyBytes, err := ioutil.ReadAll(req.Body)
	if err != nil {
		fmt.Println("Error reading the room from the request body")
		writer.WriteHeader(http.StatusInternalServerError)
	}
	var joinRequest *model.User
	json.Unmarshal(bodyBytes, &joinRequest)

	clientlist[joinRequest.Username] = *joinRequest

	return
}

func ListenToClients(writer http.ResponseWriter, req *http.Request) {
	bodyBytes, err := ioutil.ReadAll(req.Body)
	if err != nil {
		fmt.Println("Error reading the message from the request body")
		writer.WriteHeader(http.StatusInternalServerError)
	}
	var message *model.Message
	json.Unmarshal(bodyBytes, &message)

	//TODO: different chat rooms
		go addToLog(*message)
		go ServerBroadcast(*message)
}

func ServerBroadcast(m model.Message){
	//room := chatrooms[m.ChatRoom]
	for _, user := range clientlist {
		//if user.RoomIndex == m.ChatRoom{
			go Tell(&m, user)
		//}
	}

}

func Tell(msg *model.Message, user model.User){
	client := http.Client{}
	bufmsg := model.ConvertMessageToBuffer(msg)
	response, err := client.Post(fmt.Sprintf("http://localhost:%d/message", user.MessagePort),
		"application/json; charset=utf-8",
		bufmsg)

	defer response.Body.Close()
	if err != nil {
		fmt.Println(err.Error())
		return
	}
}

func addToLog(m model.Message) {
	fmt.Println(m.ReadableFormat())
	room := chatrooms[m.ChatRoom]

	room.log = room.log + m.ReadableFormat()
}

func startServer() {
	fmt.Println("Starting server...")
	// Create the HTTP server
	fmt.Println((http.ListenAndServe(":8000", nil).Error()))
}

// Create makes a new tcp server and listens for incoming requests
func createServer() {
	// create the server
	fmt.Println("Creating Server...")

	// Register our HTTP routes
	http.HandleFunc("/message", ListenToClients)
	//http.HandleFunc("/log", getLog)
	//http.HandleFunc("/user/update", updateUser)
	//http.HandleFunc("/chatrooms/list", listRooms)
	//http.HandleFunc("/chatrooms/create", createRoom)
	http.HandleFunc("/chatroom/join", joinRoom)
	//http.HandleFunc("/chatrooms/forUser", listRoomsForUser)
	//http.HandleFunc("/chatrooms/leave", leaveRoom)

	startServer()
}
func main() {
	createServer()

}
