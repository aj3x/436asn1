package main
//
//import (
//	"fmt"
//	"net/http"
//	"log"
//	"github.com/gorilla/websocket"
//	"./model"
//)
//
//
//
//
//
//
//
//var clients = make(map[*websocket.Conn]bool)
//var broadcast = make(chan model.Message) // broadcast channel
//var chatLogs = make([]string,10)
//
//var upgrader websocket.Upgrader
//
//
//type ServerClient struct{
//	username string
//	room int
//}
//
//// Send message to specific client
//func Tell(){
//
//}
//
//// Send message to all clients
//// message must be pre-formatted
//func Broadcast(msg string){
//	// for all clients
//	for k,c:= range clients{
//		if c {
//			k.WriteMessage(1,[]byte(msg))
//		}
//	}
//}
//
//// Listen for messages sent by clients
//func Listen(){
//	// do this continuously
//	for {
//		// Grab the next message from the broadcast channel
//		msg := <-broadcast
//		// Send it out to every client that is currently connected
//		for client := range clients {
//			err := client.WriteJSON(msg)
//			if err != nil {
//				log.Printf("error: %v", err)
//				client.Close()
//				delete(clients, client)
//			}
//		}
//	}
//}
//
//// List clients connected to the server
//func List() string{
//	return ""
//}
//
//// Create a new chat room
//func Create(){
//
//}
//
//// Join an existing chat room
//func Join(){
//
//}
//
//// handle all incoming clients
//func handleConnections(w http.ResponseWriter, r *http.Request) {
//	// Upgrade initial GET request to a websocket
//	ws, err := upgrader.Upgrade(w, r, nil)
//	if err != nil {
//		log.Fatal(err)
//	}
//	// Make sure we close the connection when the function returns
//	defer ws.Close()
//
//	// Register our new client
//	clients[ws] = true;//Client{"user",0}
//
//	for {
//		var msg model.Message
//		// Read in a new message as JSON and map it to a Message object
//		err := ws.ReadJSON(&msg)
//		if err != nil {
//			log.Printf("error: %v", err)
//			delete(clients, ws)
//			break
//		}
//		// Send the newly received message to the broadcast channel
//		broadcast <- msg
//	}
//}
//
//
//func main() {
//
//	fmt.Println("Starting server...")
//
//	//create a simple file server
//	fs := http.FileServer(http.Dir("../public"))
//	http.Handle("/",fs)
//
//	// websocket route configuration
//	http.HandleFunc("/ws",handleConnections)
//
//	// create main room
//	chatLogs[0] = ""
//
//	// create thread to start listening for messages
//	go Listen()
//
//
//	fmt.Println("Running.")
//	// start the server on the port 8000
//	err := http.ListenAndServe(":8000",nil)
//	if err!=nil{
//		log.Fatal("ListenandServe",err)
//	}
//
//}
