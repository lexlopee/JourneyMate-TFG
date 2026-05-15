// lib/widgets/car_3d_viewer.dart
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
  final String carType;
  final double height;
  final bool   showLabel;

  const Car3DViewer({
    super.key,
    this.carType   = 'all',
    this.height    = 150,
    this.showLabel = true,
  });

  @override
  State<Car3DViewer> createState() => _Car3DViewerState();
}

class _Car3DViewerState extends State<Car3DViewer>
    with SingleTickerProviderStateMixin {

  int _idx = 0;
  late AnimationController _ctrl;
  late Animation<double>   _anim;
  bool get _rotate => widget.carType == 'all';

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 400));
    _anim = CurvedAnimation(parent: _ctrl, curve: Curves.easeInOut);
    _ctrl.value = 1.0;
    if (_rotate) _next();
  }

  @override
  void didUpdateWidget(Car3DViewer old) {
    super.didUpdateWidget(old);
    if (old.carType != widget.carType && !_rotate) {
      _ctrl.stop();
      _ctrl.value = 1.0;
    }
  }

  void _next() {
    Future.delayed(const Duration(milliseconds: 3500), () {
      if (!mounted || !_rotate) return;
      _ctrl.reverse().then((_) {
        if (!mounted || !_rotate) return;
        setState(() {
          final opts = List<int>.generate(_allTypes.length, (i) => i)
            ..remove(_idx);
          opts.shuffle();
          _idx = opts.first;
        });
        _ctrl.forward().then((_) => _next());
      });
    });
  }

  @override
  void dispose() { _ctrl.dispose(); super.dispose(); }

  @override
  Widget build(BuildContext context) {
    final type = _rotate ? _allTypes[_idx] : widget.carType;
    final cfg  = _config[type] ?? _config['small']!;

    // El WebView de ModelViewer añade ~5px de padding abajo que no podemos
    // controlar. Usamos Transform.translate para subir el contenido 5px
    // y ClipRect para que esos 5px extra no sean visibles.
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        ClipRect(
          child: SizedBox(
            height: widget.height,
            width: double.infinity,
            child: FadeTransition(
              opacity: _anim,
              child: IgnorePointer(
                child: Transform.translate(
                  offset: const Offset(0, -6), // sube 6px para absorber el padding interno
                  child: SizedBox(
                    height: widget.height + 12, // compensa el espacio extra
                    width: double.infinity,
                    child: ModelViewer(
                      src: cfg.model,
                      alt: cfg.label,
                      autoRotate: true,
                      autoRotateDelay: 0,
                      rotationPerSecond: '20deg',
                      cameraControls: false,
                      disableZoom: true,
                      disablePan: true,
                      disableTap: true,
                      backgroundColor: Colors.transparent,
                      shadowIntensity: 0.10,
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
        if (widget.showLabel)
          FadeTransition(
            opacity: _anim,
            child: Padding(
              padding: const EdgeInsets.only(top: 2),
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
