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

        public Form1()
        {
            InitializeComponent();
            timer1.Interval = 5000; // 5 segundos
            timer1.Start();
        }

        private void RunSilent(string scriptName)
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

            psi.UseShellExecute = false;
            psi.CreateNoWindow = true;
            psi.WindowStyle = ProcessWindowStyle.Hidden;
            psi.Arguments = "/c \"" + scriptPath + "\"";


            Process.Start(psi);
        }



        // ============================================================
        // TIMER: ACTUALIZA ESTADOS CADA 5 SEGUNDOS
        // ============================================================
        private async void timer1_Tick(object sender, EventArgs e)
        {
            var dbTask = CheckPortAsync("localhost", 15433);
            var backendTask = CheckUrlAsync("http://localhost:8080/actuator/health");
            var webTask = CheckUrlAsync("http://localhost:5173");
            var emulatorTask = CheckAnyEmulatorAsync();
            var flutterTask = CheckFlutterAsync();

            chkDB.Checked = await dbTask;
            chkBackend.Checked = await backendTask;
            chkWeb.Checked = await webTask;
            chkEmulator.Checked = await emulatorTask;
        }

        // ============================================================
        // MÉTODOS ASÍNCRONOS
        // ============================================================
        private async Task<bool> CheckUrlAsync(string url)
        {
            try
            {
                var response = await http.GetAsync(url);
                return response.IsSuccessStatusCode
                       || response.StatusCode == System.Net.HttpStatusCode.Forbidden;
            }
            catch
            {
                return false;
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
            catch
            {
                return false;
            }
        }

        // ============================================================
        // DETECCIÓN AUTOMÁTICA DEL EMULADOR (ADB)
        // ============================================================
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
                catch
                {
                    return false;
                }
            });
        }

        // ============================================================
        // DETECCIÓN DE FLUTTER
        // ============================================================
        private async Task<bool> CheckFlutterAsync()
        {
            return await Task.Run(() =>
            {
                return Process.GetProcessesByName("flutter").Length > 0;
            });
        }

        // ============================================================
        // BOTONES
        // ============================================================
        private void btnWeb_Click(object sender, EventArgs e) => RunSilent("start_web.bat");
        private void btnMovil_Click(object sender, EventArgs e) => RunSilent("start_mobile.bat");
        private void btnTodo_Click(object sender, EventArgs e) => RunSilent("start_all.bat");
        private void btnStop_Click(object sender, EventArgs e) => RunSilent("launcher.bat");
        private void btnSalir_Click(object sender, EventArgs e) => Application.Exit();

        private void chkEmulator_CheckedChanged(object sender, EventArgs e)
        {

        }
    }
}
