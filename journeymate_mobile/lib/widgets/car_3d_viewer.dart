import 'package:flutter/material.dart';
import 'package:model_viewer_plus/model_viewer_plus.dart';

// Equivalente a Car3D.tsx
// Los .glb deben estar en: assets/animations/car-*.glb

const _config = {
  'small':    _CarCfg('assets/animations/car-small.glb',  'Pequeño',        1.0),
  'medium':   _CarCfg('assets/animations/car-medium.glb', 'Mediano',         1.0),
  'large':    _CarCfg('assets/animations/car-large.glb',  'Grande',          1.0),
  'suvs':     _CarCfg('assets/animations/car-suv.glb',    'SUV',             1.0),
  'premium':  _CarCfg('assets/animations/car-sport.glb',  'Premium / Sport', 1.0),
  'carriers': _CarCfg('assets/animations/car-van.glb',    'Furgoneta',       1.0),
};

const _allTypes = ['small', 'medium', 'large', 'suvs', 'premium', 'carriers'];

class _CarCfg {
  final String model;
  final String label;
  final double scale;
  const _CarCfg(this.model, this.label, this.scale);
}

class Car3DViewer extends StatefulWidget {
  final String carType;    // 'all' | 'small' | 'medium' etc.
  final double height;
  final bool interactive;
  final bool showLabel;

  const Car3DViewer({
    super.key,
    this.carType = 'all',
    this.height = 220,
    this.interactive = true,
    this.showLabel = true,
  });

  @override
  State<Car3DViewer> createState() => _Car3DViewerState();
}

class _Car3DViewerState extends State<Car3DViewer> with SingleTickerProviderStateMixin {
  int _currentIndex = 0;
  late AnimationController _fadeCtrl;
  late Animation<double> _fadeAnim;

  @override
  void initState() {
    super.initState();
    _fadeCtrl = AnimationController(vsync: this, duration: const Duration(milliseconds: 400));
    _fadeAnim = CurvedAnimation(parent: _fadeCtrl, curve: Curves.easeInOut);
    _fadeCtrl.value = 1.0;

    if (widget.carType == 'all') {
      _startRotation();
    }
  }

  void _startRotation() {
    Future.delayed(const Duration(milliseconds: 3500), () {
      if (!mounted) return;
      _fadeCtrl.reverse().then((_) {
        if (!mounted) return;
        setState(() {
          final options = List.generate(_allTypes.length, (i) => i)
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
    final type = widget.carType == 'all' ? _allTypes[_currentIndex] : widget.carType;
    final cfg = _config[type] ?? _config['small']!;

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        FadeTransition(
          opacity: _fadeAnim,
          child: SizedBox(
            height: widget.height,
            width: double.infinity,
            child: ModelViewer(
              src: cfg.model,
              alt: cfg.label,
              autoRotate: true,
              autoRotateDelay: 0,
              rotationPerSecond: '30deg',
              cameraControls: widget.interactive,
              disableZoom: true,
              disablePan: true,
              backgroundColor: Colors.transparent,
              shadowIntensity: 0.15,
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
                  fontSize: 9, fontWeight: FontWeight.w900,
                  letterSpacing: 3, color: Color(0x660F3D3A),
                ),
              ),
            ),
          ),
      ],
    );
  }
}