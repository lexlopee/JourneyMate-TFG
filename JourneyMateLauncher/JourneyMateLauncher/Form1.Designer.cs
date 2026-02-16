namespace JourneyMateLauncher
{
    partial class Form1
    {
        private System.ComponentModel.IContainer components = null;

        private System.Windows.Forms.Button btnWeb;
        private System.Windows.Forms.Button btnMovil;
        private System.Windows.Forms.Button btnTodo;
        private System.Windows.Forms.Button btnStop;
        private System.Windows.Forms.Button btnSalir;

        private System.Windows.Forms.CheckBox chkDB;
        private System.Windows.Forms.CheckBox chkBackend;
        private System.Windows.Forms.CheckBox chkWeb;
        private System.Windows.Forms.CheckBox chkEmulator;

        private System.Windows.Forms.Timer timer1;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            this.btnWeb = new System.Windows.Forms.Button();
            this.btnMovil = new System.Windows.Forms.Button();
            this.btnTodo = new System.Windows.Forms.Button();
            this.btnStop = new System.Windows.Forms.Button();
            this.btnSalir = new System.Windows.Forms.Button();
            this.chkDB = new System.Windows.Forms.CheckBox();
            this.chkBackend = new System.Windows.Forms.CheckBox();
            this.chkWeb = new System.Windows.Forms.CheckBox();
            this.chkEmulator = new System.Windows.Forms.CheckBox();
            this.timer1 = new System.Windows.Forms.Timer(this.components);
            this.SuspendLayout();
            // 
            // btnWeb
            // 
            this.btnWeb.Location = new System.Drawing.Point(27, 38);
            this.btnWeb.Name = "btnWeb";
            this.btnWeb.Size = new System.Drawing.Size(213, 38);
            this.btnWeb.TabIndex = 0;
            this.btnWeb.Text = "Arrancar Web";
            this.btnWeb.Click += new System.EventHandler(this.btnWeb_Click);
            // 
            // btnMovil
            // 
            this.btnMovil.Location = new System.Drawing.Point(27, 101);
            this.btnMovil.Name = "btnMovil";
            this.btnMovil.Size = new System.Drawing.Size(213, 38);
            this.btnMovil.TabIndex = 1;
            this.btnMovil.Text = "Arrancar Móvil";
            this.btnMovil.Click += new System.EventHandler(this.btnMovil_Click);
            // 
            // btnTodo
            // 
            this.btnTodo.Location = new System.Drawing.Point(27, 163);
            this.btnTodo.Name = "btnTodo";
            this.btnTodo.Size = new System.Drawing.Size(213, 38);
            this.btnTodo.TabIndex = 2;
            this.btnTodo.Text = "Arrancar Todo";
            this.btnTodo.Click += new System.EventHandler(this.btnTodo_Click);
            // 
            // btnStop
            // 
            this.btnStop.Location = new System.Drawing.Point(27, 230);
            this.btnStop.Name = "btnStop";
            this.btnStop.Size = new System.Drawing.Size(213, 38);
            this.btnStop.TabIndex = 3;
            this.btnStop.Text = "Parar Servicios";
            this.btnStop.Click += new System.EventHandler(this.btnStop_Click);
            // 
            // btnSalir
            // 
            this.btnSalir.Location = new System.Drawing.Point(27, 304);
            this.btnSalir.Name = "btnSalir";
            this.btnSalir.Size = new System.Drawing.Size(213, 38);
            this.btnSalir.TabIndex = 4;
            this.btnSalir.Text = "Salir";
            this.btnSalir.Click += new System.EventHandler(this.btnSalir_Click);
            // 
            // chkDB
            // 
            this.chkDB.AutoSize = true;
            this.chkDB.Enabled = false;
            this.chkDB.Location = new System.Drawing.Point(27, 360);
            this.chkDB.Name = "chkDB";
            this.chkDB.Size = new System.Drawing.Size(96, 17);
            this.chkDB.TabIndex = 5;
            this.chkDB.Text = "Base de Datos";
            // 
            // chkBackend
            // 
            this.chkBackend.Enabled = false;
            this.chkBackend.Location = new System.Drawing.Point(27, 383);
            this.chkBackend.Name = "chkBackend";
            this.chkBackend.Size = new System.Drawing.Size(104, 24);
            this.chkBackend.TabIndex = 6;
            this.chkBackend.Text = "Backend (Spring Boot)";
            // 
            // chkWeb
            // 
            this.chkWeb.Enabled = false;
            this.chkWeb.Location = new System.Drawing.Point(152, 385);
            this.chkWeb.Name = "chkWeb";
            this.chkWeb.Size = new System.Drawing.Size(104, 24);
            this.chkWeb.TabIndex = 7;
            this.chkWeb.Text = "Web (React)";
            // 
            // chkEmulator
            // 
            this.chkEmulator.AutoSize = true;
            this.chkEmulator.Enabled = false;
            this.chkEmulator.Location = new System.Drawing.Point(152, 360);
            this.chkEmulator.Name = "chkEmulator";
            this.chkEmulator.Size = new System.Drawing.Size(109, 17);
            this.chkEmulator.TabIndex = 8;
            this.chkEmulator.Text = "Emulador Android";
            this.chkEmulator.CheckedChanged += new System.EventHandler(this.chkEmulator_CheckedChanged);
            // 
            // timer1
            // 
            this.timer1.Interval = 2000;
            this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
            // 
            // Form1
            // 
            this.ClientSize = new System.Drawing.Size(268, 520);
            this.Controls.Add(this.btnWeb);
            this.Controls.Add(this.btnMovil);
            this.Controls.Add(this.btnTodo);
            this.Controls.Add(this.btnStop);
            this.Controls.Add(this.btnSalir);
            this.Controls.Add(this.chkDB);
            this.Controls.Add(this.chkBackend);
            this.Controls.Add(this.chkWeb);
            this.Controls.Add(this.chkEmulator);
            this.Name = "Form1";
            this.Text = "JourneyMate ";
            this.ResumeLayout(false);
            this.PerformLayout();

        }
    }
}
