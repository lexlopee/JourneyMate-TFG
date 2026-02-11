using System;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;

namespace JourneyMateLauncher
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }
        private void RunSilent(string scriptName)
        {
            // Ir SIEMPRE a la carpeta del proyecto, no al bin
            string projectRoot = Directory.GetParent(
                Directory.GetParent(
                    Directory.GetParent(
                        AppDomain.CurrentDomain.BaseDirectory
                    ).FullName
                ).FullName
            ).FullName;

            string scriptFolder = Path.Combine(projectRoot, @"scripts\windows");
            string scriptPath = Path.Combine(scriptFolder, scriptName);

            if (!File.Exists(scriptPath))
            {
                MessageBox.Show("No se encontró el script: " + scriptPath);
                return;
            }

            var psi = new ProcessStartInfo();
            psi.FileName = scriptPath;
            psi.CreateNoWindow = true;
            psi.UseShellExecute = false;
            psi.WindowStyle = ProcessWindowStyle.Hidden;

            Process.Start(psi);
        }



        private void btnWeb_Click(object sender, EventArgs e)
        {
            RunSilent("start_web.bat");
        }

        private void btnMovil_Click(object sender, EventArgs e)
        {
            RunSilent("start_mobile.bat");
        }

        private void btnTodo_Click(object sender, EventArgs e)
        {
            RunSilent("start_all.bat");   // ← CORREGIDO
        }

        private void btnStop_Click(object sender, EventArgs e)
        {
            RunSilent("launcher.bat");    // ← CORREGIDO
        }

        private void btnSalir_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }
    }
}
