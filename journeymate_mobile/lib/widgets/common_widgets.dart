import 'package:flutter/material.dart';
import 'package:lucide_icons/lucide_icons.dart';
import '../core/app_colors.dart';

class LoadingModal extends StatelessWidget {
  const LoadingModal({super.key});
  @override
  Widget build(BuildContext context) => Container(
    height: 300,
    decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(40))
    ),
    child: const Center(child: CircularProgressIndicator(color: AppColors.teal600)),
  );
}

class ErrorModal extends StatelessWidget {
  final String message;
  const ErrorModal({super.key, required this.message});

  @override
  Widget build(BuildContext context) => Container(
    height: 300,
    width: double.infinity,
    padding: const EdgeInsets.all(24),
    decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(40))
    ),
    child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
      const Icon(LucideIcons.alertTriangle, size: 48, color: Colors.redAccent),
      const SizedBox(height: 16),
      const Text("¡UPS! ALGO SALIÓ MAL", style: TextStyle(fontSize: 14, fontWeight: FontWeight.w900, color: AppColors.teal900)),
      const SizedBox(height: 8),
      Text(message, textAlign: TextAlign.center, style: const TextStyle(color: Colors.grey, fontSize: 12)),
      const SizedBox(height: 24),
      TextButton(onPressed: () => Navigator.pop(context), child: const Text("CERRAR")),
    ]),
  );
}