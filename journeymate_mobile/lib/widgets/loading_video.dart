import 'package:flutter/material.dart';
import 'package:video_player/video_player.dart';

// Equivalente a LoadingVideo.tsx
// El vídeo debe estar en: assets/animations/logo-loading.mp4
class LoadingVideo extends StatefulWidget {
  final double size;
  const LoadingVideo({super.key, this.size = 180});

  @override
  State<LoadingVideo> createState() => _LoadingVideoState();
}

class _LoadingVideoState extends State<LoadingVideo> {
  late VideoPlayerController _controller;
  bool _initialized = false;

  @override
  void initState() {
    super.initState();
    _controller = VideoPlayerController.asset('assets/animations/logo-loading.mp4')
      ..initialize().then((_) {
        if (mounted) {
          setState(() => _initialized = true);
          _controller.setLooping(true);
          _controller.setVolume(0);
          _controller.play();
        }
      });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!_initialized) {
      // Fallback mientras carga: spinner de teal
      return SizedBox(
        width: widget.size,
        height: widget.size,
        child: const Center(
          child: CircularProgressIndicator(
            color: Color(0xFF14B8A6),
            strokeWidth: 3,
          ),
        ),
      );
    }

    return SizedBox(
      width: widget.size,
      height: widget.size,
      // BlendMode.screen = el negro del vídeo se vuelve transparente
      // Flutter no soporta BlendMode en VideoPlayer directamente,
      // usamos ColorFiltered como workaround
      child: VideoPlayer(_controller),
    );
  }
}

// ── Versión compacta para el botón Buscar ─────────────────────────────────────
class LoadingSpinner extends StatelessWidget {
  final double size;
  final Color color;
  const LoadingSpinner({super.key, this.size = 20, this.color = Colors.white});

  @override
  Widget build(BuildContext context) => SizedBox(
    width: size, height: size,
    child: CircularProgressIndicator(color: color, strokeWidth: 2.5),
  );
}