using System;
using System.Diagnostics;
using System.IO;
using System.Net.Http;
using System.Net.Sockets;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace JourneyMateLauncher
{
    public partial class Form1 : Form
    {
        private readonly HttpClient http = new HttpClient();
        private bool backendStartedByUser = false; // controla si tú lo arrancaste

        public Form1()
        {
            InitializeComponent();
            timer1.Interval = 5000;
            timer1.Start();
        }

        // ============================================================
        // EJECUTAR SCRIPT EN TERMINAL VISIBLE (CON LOGS)
        // ============================================================
         private void RunVisible(string scriptName)
        {
            string projectRoot = Directory.GetParent(
                Directory.GetParent(
                    Directory.GetParent(
                        AppDomain.CurrentDomain.BaseDirectory
                    ).FullName
                ).FullName
            ).FullName;

            string scriptFolder = Path.Combine(projectRoot, @"scripts\windows");
            string scriptPath = Path.Combine(scriptFolder, scriptName);

            var psi = new ProcessStartInfo();
            psi.FileName = "cmd.exe";
            psi.Arguments = "/k \"" + scriptPath + "\"";
            psi.UseShellExecute = true;

            Process.Start(psi);
        }
        

         /*private void RunHidden(string scriptName)
         {
            string projectRoot = Directory.GetParent(
                Directory.GetParent(
                    Directory.GetParent(
                        AppDomain.CurrentDomain.BaseDirectory
                    ).FullName
                ).FullName
            ).FullName;

            string scriptFolder = Path.Combine(projectRoot, @"scripts\windows");
            string scriptPath = Path.Combine(scriptFolder, scriptName);

            var psi = new ProcessStartInfo();
            psi.FileName = scriptPath;                 
            psi.UseShellExecute = false;               
            psi.CreateNoWindow = true;                 
            psi.WindowStyle = ProcessWindowStyle.Hidden;

            Process.Start(psi);
        }*/

        // ============================================================
        // TIMER: SOLO DESACTIVA chkBackend SI EL BACKEND SE APAGA
        // ============================================================
        private async void timer1_Tick(object sender, EventArgs e)
        {
            chkDB.Checked = await CheckPortAsync("localhost", 15433);
            bool backendAlive = await CheckPortAsync("localhost", 8080);
            chkBackend.Checked = backendAlive;

            chkWeb.Checked = await CheckUrlAsync("http://localhost:5173");
            chkEmulator.Checked = await CheckAnyEmulatorAsync();
        }


        // ============================================================
        // MÉTODOS ASÍNCRONOS
        // ============================================================
        private async Task<bool> CheckUrlAsync(string url)
        {
            try
            {
                var response = await http.GetAsync(url);

                // Si responde algo, aunque sea 403 o 503, significa que el backend está vivo
                return response.StatusCode == System.Net.HttpStatusCode.OK
                    || response.StatusCode == System.Net.HttpStatusCode.Forbidden
                    || response.StatusCode == System.Net.HttpStatusCode.ServiceUnavailable;
            }
            catch
            {
                return false; // No responde → backend apagado
            }
        }


        private async Task<bool> CheckPortAsync(string host, int port)
        {
            try
            {
                using (var client = new TcpClient())
                {
                    var task = client.ConnectAsync(host, port);
                    var result = await Task.WhenAny(task, Task.Delay(1000));
                    return result == task && client.Connected;
                }
            }
            catch { return false; }
        }

        private async Task<bool> CheckAnyEmulatorAsync()
        {
            return await Task.Run(() =>
            {
                try
                {
                    var process = new Process();
                    process.StartInfo.FileName = "adb";
                    process.StartInfo.Arguments = "devices";
                    process.StartInfo.RedirectStandardOutput = true;
                    process.StartInfo.UseShellExecute = false;
                    process.StartInfo.CreateNoWindow = true;
                    process.Start();

                    string output = process.StandardOutput.ReadToEnd();
                    return output.Contains("emulator-");
                }
                catch { return false; }
            });
        }

        // ============================================================
        // BOTONES
        // ============================================================
        private void btnBackend_Click(object sender, EventArgs e)
        {
            //RunHidden("start_docker.bat");
            //RunHidden("start_backend.bat");
            RunVisible("start_docker.bat");   // Arranca Docker + BD
            RunVisible("start_backend.bat");  // Arranca el backend
            chkBackend.Checked = true;        // Activa el check
            backendStartedByUser = true;      // Marca que tú lo arrancaste
        }

        //Cambiar a RunHidden para no ver las terminales
        private void btnWeb_Click(object sender, EventArgs e) => RunVisible("start_web.bat");
        private void btnMovil_Click(object sender, EventArgs e) => RunVisible("start_mobile.bat");
        private void btnTodo_Click(object sender, EventArgs e) => RunVisible("start_all.bat");
        private void btnStop_Click(object sender, EventArgs e) => RunVisible("launcher.bat");
        private void btnSalir_Click(object sender, EventArgs e) => Application.Exit();

        private void chkEmulator_CheckedChanged(object sender, EventArgs e)
        { 
        }

    }
}
