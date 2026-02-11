namespace JourneyMateLauncher
{
    partial class Form1
    {
        /// <summary>
        /// Variable del diseñador necesaria.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Limpiar los recursos que se estén usando.
        /// </summary>
        /// <param name="disposing">true si los recursos administrados se deben desechar; false en caso contrario.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Código generado por el Diseñador de Windows Forms

        /// <summary>
        /// Método necesario para admitir el Diseñador. No se puede modificar
        /// el contenido de este método con el editor de código.
        /// </summary>
        private void InitializeComponent()
        {
            this.btnWeb = new System.Windows.Forms.Button();
            this.btnMovil = new System.Windows.Forms.Button();
            this.btnTodo = new System.Windows.Forms.Button();
            this.btnStop = new System.Windows.Forms.Button();
            this.btnSalir = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // btnWeb
            // 
            this.btnWeb.Location = new System.Drawing.Point(27, 38);
            this.btnWeb.Name = "btnWeb";
            this.btnWeb.Size = new System.Drawing.Size(213, 38);
            this.btnWeb.TabIndex = 0;
            this.btnWeb.Text = "Arrancar Web";
            this.btnWeb.UseVisualStyleBackColor = true;
            this.btnWeb.Click += new System.EventHandler(this.btnWeb_Click);
            // 
            // btnMovil
            // 
            this.btnMovil.Location = new System.Drawing.Point(27, 101);
            this.btnMovil.Name = "btnMovil";
            this.btnMovil.Size = new System.Drawing.Size(213, 38);
            this.btnMovil.TabIndex = 1;
            this.btnMovil.Text = "Arrancar Móvil";
            this.btnMovil.UseVisualStyleBackColor = true;
            this.btnMovil.Click += new System.EventHandler(this.btnMovil_Click);
            // 
            // btnTodo
            // 
            this.btnTodo.Location = new System.Drawing.Point(27, 163);
            this.btnTodo.Name = "btnTodo";
            this.btnTodo.Size = new System.Drawing.Size(213, 38);
            this.btnTodo.TabIndex = 2;
            this.btnTodo.Text = "Arrancar Todo";
            this.btnTodo.UseVisualStyleBackColor = true;
            this.btnTodo.Click += new System.EventHandler(this.btnTodo_Click);
            // 
            // btnStop
            // 
            this.btnStop.Location = new System.Drawing.Point(27, 230);
            this.btnStop.Name = "btnStop";
            this.btnStop.Size = new System.Drawing.Size(213, 38);
            this.btnStop.TabIndex = 3;
            this.btnStop.Text = "Para Servicios";
            this.btnStop.UseVisualStyleBackColor = true;
            this.btnStop.Click += new System.EventHandler(this.btnStop_Click);
            // 
            // btnSalir
            // 
            this.btnSalir.Location = new System.Drawing.Point(27, 304);
            this.btnSalir.Name = "btnSalir";
            this.btnSalir.Size = new System.Drawing.Size(213, 38);
            this.btnSalir.TabIndex = 4;
            this.btnSalir.Text = "Salir";
            this.btnSalir.UseVisualStyleBackColor = true;
            this.btnSalir.Click += new System.EventHandler(this.btnSalir_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(268, 391);
            this.Controls.Add(this.btnSalir);
            this.Controls.Add(this.btnStop);
            this.Controls.Add(this.btnTodo);
            this.Controls.Add(this.btnMovil);
            this.Controls.Add(this.btnWeb);
            this.Name = "Form1";
            this.Text = "Menú";
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button btnWeb;
        private System.Windows.Forms.Button btnMovil;
        private System.Windows.Forms.Button btnTodo;
        private System.Windows.Forms.Button btnStop;
        private System.Windows.Forms.Button btnSalir;
    }
}

