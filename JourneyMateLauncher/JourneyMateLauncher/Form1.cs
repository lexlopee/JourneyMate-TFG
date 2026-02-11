using System;
using System.Diagnostics;
using System.Windows.Forms;

namespace JourneyMateLauncher
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void RunSilent(string scriptPath)
        {
            var psi = new ProcessStartInfo();
            psi.FileName = scriptPath;
            psi.CreateNoWindow = true;
            psi.UseShellExecute = false;
            psi.WindowStyle = ProcessWindowStyle.Hidden;

            Process.Start(psi);
        }

        private void btnWeb_Click(object sender, EventArgs e)
        {
            RunSilent("scripts\\windows\\start_web.bat");
        }

        private void btnMovil_Click(object sender, EventArgs e)
        {
            RunSilent("scripts\\windows\\start_mobile.bat");
        }

        private void btnTodo_Click(object sender, EventArgs e)
        {
            RunSilent("scripts\\windows\\start_all.bat");
        }

        private void btnStop_Click(object sender, EventArgs e)
        {
            RunSilent("scripts\\windows\\launcher.bat");
        }

        private void btnSalir_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }
    }
}
