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
        String username = "alex";
        String header = "GET / HTTP/1.0";
        TcpClient client = new TcpClient();
        NetworkStream serverStream = default(NetworkStream);
        

        Thread readServer;

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

            StreamWriter writer = new StreamWriter(client.GetStream());
            //byte[] outStream = Encoding.ASCII.GetBytes(textBox.Text + "$");
            //serverStream.Write(header, 0, header.Length);
            //serverStream.Write(outStream, 0, outStream.Length);
            //serverStream.Flush() ;
            writer.WriteLine(header);
            writer.WriteLine(textBox.Text);
            writer.Flush();
            textBox.Text = "";
        }
        
        private void button2_Click(object sender, EventArgs e)
        {
            readData = "Connecting to Chat Server ...";
            client = new TcpClient("localhost", 8888);
            if (!client.Connected)
                throw new Exception();

            readData = "Connected to Chat Server.";


            serverStream = client.GetStream();
            new Thread(getMessage).Start();

            


            /*
            byte[] outStream = Encoding.ASCII.GetBytes(outBox.Text + "$");
            serverStream.Write(outStream, 0, outStream.Length);
            serverStream.Flush();

            Thread ctThread = new Thread(getMessage);
            ctThread.Start();
            */
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
                    readData = readData.Replace("\n", Environment.NewLine);

                    msg();
                }
                /*
                serverStream = client.GetStream();
                //buffSize = client.ReceiveBufferSize;
                serverStream.Read(inStream, 0, 10);
                string returndata = Encoding.ASCII.GetString(inStream);
                readData = "" + returndata;
                msg();
                */
            }
        }

        private void LeaveServer(object sender, EventArgs e)
        {
            StreamWriter writer = new StreamWriter(client.GetStream());
            writer.WriteLine(header);
            writer.WriteLine("/exit");
            writer.Flush();
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
