using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Net.Sockets;
using System.Threading;
using System.IO;

namespace WindowsFormsApp1
{
    public partial class Form1 : Form
    {
        //String username = "alex";
        String header = "GET / HTTP/1.0";
        TcpClient client = new TcpClient();
        NetworkStream serverStream = default(NetworkStream);
        bool connected = false;
        

        //Thread readServer;

        string readData = null;
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar.Equals(Keys.Enter))
            {
                button1_Click(sender, null);
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                if (client.Connected)
                {
                    StreamWriter writer = new StreamWriter(client.GetStream());
                    writer.WriteLine(header);
                    string send = textBox.Text;
                    send = send.Replace("\r\n", " ");
                    send = send.Replace("\n", " ");
                    writer.WriteLine(send);
                    writer.Flush();
                }
            }catch(IOException)
            {
                connected = false;
                client.Close();
                serverStream.Close();
                outBox.AppendText("Disconnected from server."+Environment.NewLine);
            }
            textBox.Text = "";
        }
        
        private void button2_Click(object sender, EventArgs e)
        {
            //try
            
                if (!connected)
                {
                    label1.Text = "Server Name";
                    readData = "Connecting to Chat Server ...";
                    client = new TcpClient("localhost", 8888);
                    if (!client.Connected)
                        throw new Exception();

                    connected = true;
                    readData = "Connected to Chat Server.";

                    serverStream = client.GetStream();
                    StreamWriter writer = new StreamWriter(client.GetStream());
                    writer.WriteLine(header);
                    writer.WriteLine(serverBox.Text);
                    writer.Flush();
                    serverBox.Text = "";

                    new Thread(getMessage).Start();
                }
            //}
            


            /*
            byte[] outStream = Encoding.ASCII.GetBytes(outBox.Text + "$");
            serverStream.Write(outStream, 0, outStream.Length);
            serverStream.Flush();

            Thread ctThread = new Thread(getMessage);
            ctThread.Start();
            */
        }

        /**
         * Gets messages asynchronously from the server
         * 
         */
        private void getMessage()
        {
            // continously do this while connected
            while (connected)
            {
                //get size of buffer for byte stream
                int buffSize = 0;
                buffSize = client.ReceiveBufferSize;
                byte[] inStream = new byte[buffSize];

                //if there is new data available display it
                if (serverStream.DataAvailable)
                {
                    // read stream and trim ending
                    serverStream.Read(inStream, 0, buffSize);
                    string rData = System.Text.Encoding.ASCII.GetString(inStream).TrimEnd('\0');

                    readData = rData;
                    // modify text to replace newline with system newline
                    readData = readData.Replace("\n", Environment.NewLine);


                    // display messages from server
                    msg();
                }
            }
        }
        
        /**
         * Disconnect from server
         */
        private void DisconnectServer()
        {
            if (connected && client.Connected)
            {
                connected = false;
                StreamWriter writer = new StreamWriter(client.GetStream());
                writer.WriteLine(header);
                writer.WriteLine("/exit");
                writer.Flush();

                writer.Close();
                serverStream.Close();
            }

            client.Close();

        }
        private void LeaveServer(object sender, EventArgs e)
        {
            this.DisconnectServer();
            //this.Close();
            Application.Exit();
        }
        private void msg()
        {
            if (this.InvokeRequired)
            {
                this.Invoke(new MethodInvoker(msg));
            }
            else
            {
                outBox.Text = outBox.Text + readData;
            }
        }
        private void Form1_Load(object sender, EventArgs e)
        {
            this.sendBtn.Click += button1_Click;
            this.connectBtn.Click += button2_Click;
            FormClosing += LeaveServer;
        }

        private void textBox_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                button1_Click(sender, null);
            }
        }
    }
}
