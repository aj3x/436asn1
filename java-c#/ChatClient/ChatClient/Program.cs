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
        String name;
        TcpClient client = new TcpClient();
        NetworkStream serverStream = default(NetworkStream);
        
        Thread write;
        string readData = null;
        public Program()
        {
            Console.Write("Enter username: ");
            name = Console.ReadLine();



            client = new TcpClient("localhost", 8888);
            serverStream = client.GetStream();

            new Thread(this.getMessage);
        }

        private void readMessages()
        {
            StreamReader reader = new StreamReader(client.GetStream());
            StreamWriter writer = new StreamWriter(client.GetStream());
            String send = name;
            String serverMsg = "";
            while (!send.Equals("/exit"))
            {
                //Console.WriteLine(reader.ReadToEnd());
                //Console.SetCursorPosition(0, 20);
                //int x = reader.Read();
                writer.WriteLine("GET / HTTP/1.0");
                writer.WriteLine(send);
                writer.Flush();

                send = Console.ReadLine();

                //Console.Clear();

                //Stream serverStream = client.GetStream();
                
                int buffSize = 0;
                buffSize = client.ReceiveBufferSize;
                byte[] inStream = new byte[20];
                serverStream.Read(inStream, 0, 19);
                
                string rData = System.Text.Encoding.ASCII.GetString(inStream);
                readData = "" + rData;
                //readData = new StreamReader(serverStream).ReadLine();

                Console.WriteLine(readData);


                //serverMsg = reader.ReadLine();
                //Console.WriteLine(serverMsg);

            }

            writer.WriteLine();
        }

        private void getMessage()
        {
            while (true)
            {
                //serverStream = client.GetStream();
                int buffSize = 0;
                //byte[] inStream = new byte[10025];
                //buffSize = client.ReceiveBufferSize;


                //serverStream.Read(inStream, 0, buffSize);
                //string returndata = Encoding.ASCII.GetString(inStream);
                //readData = "" + returndata;

                readData = "size: " + buffSize;

                msg();
            }
        }

        private void msg()
        {
            Console.WriteLine(readData);
        }
       
        static void Main(string[] args)
        {
            Program lol = new Program();
            lol.readMessages();
        }
    }
}
