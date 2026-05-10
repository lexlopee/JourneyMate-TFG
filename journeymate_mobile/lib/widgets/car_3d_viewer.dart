// lib/widgets/car_3d_viewer.dart
//
// FIXES:
// 1. ✅ Cuando carType != 'all' muestra SIEMPRE el modelo correcto (no rota)
// 2. ✅ Cuando carType == 'all' rota entre todos los modelos
// 3. ✅ Overflow corregido con SizedBox de tamaño fijo

import 'package:flutter/material.dart';
import 'package:model_viewer_plus/model_viewer_plus.dart';

const _config = <String, _CarCfg>{
  'small':    _CarCfg('assets/animations/car-small.glb',  'Pequeño'),
  'medium':   _CarCfg('assets/animations/car-medium.glb', 'Mediano'),
  'large':    _CarCfg('assets/animations/car-large.glb',  'Grande'),
  'suvs':     _CarCfg('assets/animations/car-suv.glb',    'SUV'),
  'premium':  _CarCfg('assets/animations/car-sport.glb',  'Premium'),
  'carriers': _CarCfg('assets/animations/car-van.glb',    'Furgoneta'),
};

const _allTypes = ['small', 'medium', 'large', 'suvs', 'premium', 'carriers'];

class _CarCfg {
  final String model, label;
  const _CarCfg(this.model, this.label);
}

class Car3DViewer extends StatefulWidget {
  final String carType;   // 'all' | 'small' | 'medium' | 'large' | 'suvs' | 'premium' | 'carriers'
  final double height;
  final bool   interactive;
  final bool   showLabel;

  const Car3DViewer({
    super.key,
    this.carType    = 'all',
    this.height     = 180,
    this.interactive = true,
    this.showLabel  = true,
  });

  @override
  State<Car3DViewer> createState() => _Car3DViewerState();
}

class _Car3DViewerState extends State<Car3DViewer>
    with SingleTickerProviderStateMixin {

  int _currentIndex = 0;
  late AnimationController _fadeCtrl;
  late Animation<double>   _fadeAnim;

  // FIX: sólo rotamos cuando carType == 'all'
  bool get _shouldRotate => widget.carType == 'all';

  @override
  void initState() {
    super.initState();
    _fadeCtrl = AnimationController(vsync: this, duration: const Duration(milliseconds: 400));
    _fadeAnim = CurvedAnimation(parent: _fadeCtrl, curve: Curves.easeInOut);
    _fadeCtrl.value = 1.0;

    if (_shouldRotate) _startRotation();
  }

  // FIX: cuando carType cambia externamente, no rotamos si tiene un tipo fijo
  @override
  void didUpdateWidget(Car3DViewer old) {
    super.didUpdateWidget(old);
    if (old.carType != widget.carType) {
      if (!_shouldRotate) {
        // Parar la rotación si pasamos de 'all' a un tipo específico
        _fadeCtrl.stop();
        _fadeCtrl.value = 1.0;
      }
    }
  }

  void _startRotation() {
    Future.delayed(const Duration(milliseconds: 3500), () {
      if (!mounted || !_shouldRotate) return;
      _fadeCtrl.reverse().then((_) {
        if (!mounted || !_shouldRotate) return;
        setState(() {
          final options = List<int>.generate(_allTypes.length, (i) => i)
            ..remove(_currentIndex);
          options.shuffle();
          _currentIndex = options.first;
        });
        _fadeCtrl.forward().then((_) => _startRotation());
      });
    });
  }

  @override
  void dispose() {
    _fadeCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // FIX: si hay un tipo específico, lo usamos directamente
    final type = _shouldRotate
        ? _allTypes[_currentIndex]
        : widget.carType;

    // Fallback a 'small' si el tipo no está en la config
    final cfg = _config[type] ?? _config['small']!;

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        FadeTransition(
          opacity: _fadeAnim,
          child: SizedBox(
            // FIX: tamaño fijo para evitar overflow
            height: widget.height,
            width: double.infinity,
            child: ModelViewer(
              src: cfg.model,
              alt: cfg.label,
              autoRotate: true,
              autoRotateDelay: 0,
              rotationPerSecond: '25deg',
              cameraControls: widget.interactive,
              disableZoom: true,
              disablePan: true,
              backgroundColor: Colors.transparent,
              shadowIntensity: 0.12,
            ),
          ),
        ),
        if (widget.showLabel)
          FadeTransition(
            opacity: _fadeAnim,
            child: Padding(
              padding: const EdgeInsets.only(top: 4),
              child: Text(
                cfg.label.toUpperCase(),
                style: const TextStyle(
                  fontSize: 9,
                  fontWeight: FontWeight.w900,
                  letterSpacing: 3,
                  color: Color(0x660F3D3A),
                ),
              ),
            ),
          ),
      ],
    );
  }
}