using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.IO;

namespace ChatClient
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.Write("Enter username: ");
            string name = "Alex";

            name = Console.ReadLine();
            TcpClient client = new TcpClient("localhost", 8888);
            NetworkStream serverStream = default(NetworkStream);
            string readData = null;

            StreamReader reader = new StreamReader(client.GetStream());
            StreamWriter writer = new StreamWriter(client.GetStream());

            
            String send = name;
            while (!send.Equals("/exit"))
            {
                //Console.WriteLine(reader.ReadToEnd());

                writer.WriteLine("GET / HTTP/1.0");
                writer.WriteLine(send);
                writer.Flush();
                send = Console.ReadLine();
                Console.Clear();
            }
            writer.WriteLine();
            
        }
    }
}
