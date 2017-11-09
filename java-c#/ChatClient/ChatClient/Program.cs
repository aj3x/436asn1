using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Threading;

namespace ChatClient
{
    class Program
    {
        String username;
        TcpClient client = new TcpClient();
        NetworkStream serverStream = default(NetworkStream);

        Thread readServer;
        string readData = null;
        public Program()
        {


            bool tempBool = false;
            username = "";
            while (username.Equals("") || username.Contains("/") || username.Contains(" "))
            {
                if (tempBool)
                    Console.WriteLine("invalid username: can't contain: empty space or '/'");
                else
                    tempBool = true;
                Console.Write("Enter a username:");
                username = Console.ReadLine();
            }
            Console.WriteLine("Got username:" + username);
            client = new TcpClient("localhost", 8888);

            if (!client.Connected)
                throw new Exception();

            serverStream = client.GetStream();
            new Thread(getMessage).Start();

            //Thread writeServer = new Thread(this.readMessages);
            //writeServer.Start();

            //readServer = new Thread(this.getMessage);
        }

        private void readMessages()
        {
            StreamReader reader = new StreamReader(client.GetStream());
            StreamWriter writer = new StreamWriter(client.GetStream());
            String send = this.username;



            


            while (!send.Equals("/exit"))
            {
                writer.WriteLine("GET / HTTP/1.0");
                writer.WriteLine(send);
                writer.Flush();

                send = Console.ReadLine();

            }

            //writer.WriteLine(send);

            writer.WriteLine();
        }

        private void getMessage()
        {


            while (true)
            {
                int buffSize = 0;
                buffSize = client.ReceiveBufferSize;
                byte[] inStream = new byte[buffSize];
                if (serverStream.DataAvailable)
                {
                    serverStream.Read(inStream, 0, buffSize);
                    string rData = System.Text.Encoding.ASCII.GetString(inStream).TrimEnd('\0');

                    readData = "" + rData;
                    //readData = new StreamReader(serverStream).ReadLine();

                    Console.WriteLine(readData);
                }
            }
        }
       
        static void Main(string[] args)
        {
            Program lol = new Program();
            lol.readMessages();
            
        }
    }
}
