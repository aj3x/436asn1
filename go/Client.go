package main
//
//import (
//	"net"
//	"strings"
//)
//
///* type 1
//import (
//	"fmt"
//	"bufio"
//	"os"
//	"./model"
//	"strings"
//	"net/http"
//	"net"
//)
//
//type Client struct{
//	user *model.User
//	HTTPClient *http.Client
//	Conn *net.Conn
//}
//
//func constructMessage(chatRoomNum int, userName string, body string) *model.Message {
//	// Marshal the message, and prepare it for transit
//	message := &model.Message{ChatRoom: chatRoomNum, Username: userName, Body: strings.TrimSpace(body)}
//	return message
//}
//
//func (client *Client) sendMessage(chatRoomNum int, text string) {
//	// Format the message for serialization
//	m := constructMessage(chatRoomNum, client.user.Username, text)
//	messageBuffer := model.ConvertMessageToBuffer(m)
//	resp, err := client.HTTPClient.Post("http://localhost:8000/message", "application/json; charset=utf-8", messageBuffer)
//	defer resp.Body.Close()
//	if err != nil {
//		fmt.Println(err.Error())
//		return
//	}
//}
//
//func getMessages(){
//	fmt.Println()
//}
//
//func CreateClient() *Client{
//	fmt.Println("Creating client...")
//	// Create the client
//	client := &Client{
//		HTTPClient: &http.Client{},
//		user:       &model.User{},
//	}
//	return client
//}
//
//func main() {
//	buf := bufio.NewReader(os.Stdin)
//	temp := fmt.Scanln()
//	me := model.User{}
//	buf.ReadLine()
//	fmt.Println("sweet")
//}
//*/
//import(
//	"net/http"
//	"./model"
//
//	"github.com/gorilla/websocket"
//
//	"time"
//	"log"
//	"bytes"
//)
//const (
//	// Time allowed to write a message to the peer.
//	writeWait = 10 * time.Second
//
//	// Time allowed to read the next pong message from the peer.
//	pongWait = 60 * time.Second
//
//	// Send pings to peer with this period. Must be less than pongWait.
//	pingPeriod = (pongWait * 9) / 10
//
//	// Maximum message size allowed from peer.
//	maxMessageSize = 512
//)
//
//var (
//	newline = []byte{'\n'}
//	space   = []byte{' '}
//)
//
//var cupgrader = websocket.Upgrader{
//	ReadBufferSize:  1024,
//	WriteBufferSize: 1024,
//}
//
//type Client struct {
//	User       *model.User
//
//	// The websocket connection.
//	conn *websocket.Conn
//
//	// Buffered channel of outbound messages.
//	send chan []byte
//}
//
//func (c *Client) readServer() {
//	defer func() {
//		c.conn.Close()
//	}()
//	c.conn.SetReadLimit(maxMessageSize)
//	c.conn.SetReadDeadline(time.Now().Add(pongWait))
//	c.conn.SetPongHandler(func(string) error { c.conn.SetReadDeadline(time.Now().Add(pongWait)); return nil })
//	for {
//		_, message, err := c.conn.ReadMessage()
//		if err != nil {
//			if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway) {
//				log.Printf("error: %v", err)
//			}
//			break
//		}
//		message = bytes.TrimSpace(bytes.Replace(message, newline, space, -1))
//		c.hub.broadcast <- message
//	}
//}
//
//func ListenToServer(){
//
//}
//
//func main(){
//	http.HandleFunc("/ws", handleConnections)
//	http.Client{}
//}
//
