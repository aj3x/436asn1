package model

import(
	"bytes"
	"encoding/json"
)

type Message struct{
	ChatRoom int `json:"chatroom"`
	Username string `json:"username"`
	Body string `json:"body"`
}


// ReadableFormat returns a human-friendly representation of the message
func (message *Message) ReadableFormat() string {
	return message.Username + ": " + message.Body + "\n"
}

// ConvertMessageToBuffer takes a message and then returns it as a byte buffer
func ConvertMessageToBuffer(message *Message) *bytes.Buffer {
	b := new(bytes.Buffer)
	json.NewEncoder(b).Encode(&message)
	return b
}
