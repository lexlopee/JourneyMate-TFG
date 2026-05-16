import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/date_symbol_data_local.dart';

import 'core/app_colors.dart';
import 'services/auth_service.dart';

import 'screens/app_shell.dart';
import 'screens/login_screen.dart';
import 'screens/register_screen.dart';
import 'screens/payment/payment_screens.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await initializeDateFormatting('es_ES', null);

  await SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);

  SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
    statusBarColor: Colors.transparent,
    statusBarIconBrightness: Brightness.light, // blanco sobre gradiente oscuro
    systemNavigationBarColor: Colors.white,
    systemNavigationBarIconBrightness: Brightness.dark,
  ));

  await AuthService.restoreSession();
  runApp(const JourneyMateApp());
}

// ── Router ────────────────────────────────────────────────────────────────────
final GoRouter _router = GoRouter(
  initialLocation: '/',
  routes: [
    GoRoute(
      path: '/',
      builder: (_, __) => const AppShell(initialIndex: 0),
    ),
    GoRoute(
      path: '/buscar',
      builder: (_, state) {
        return const AppShell(
        initialIndex: 1,

      );
      },
    ),
    GoRoute(
      path: '/mis-reservas',
      builder: (_, __) => const AppShell(initialIndex: 2),
    ),
    GoRoute(
      path: '/perfil',
      builder: (_, __) => const AppShell(initialIndex: 3),
    ),

    GoRoute(path: '/login',    builder: (_, __) => const LoginScreen()),
    GoRoute(path: '/register', builder: (_, __) => const RegisterScreen()),
    GoRoute(
      path: '/pago-exitoso',
      builder: (_, state) => PaymentSuccessScreen(
        reservaId: state.uri.queryParameters['reservaId'],
        metodo:    state.uri.queryParameters['metodo'],
      ),
    ),
    GoRoute(path: '/pago-cancelado', builder: (_, __) => const PaymentCancelledScreen()),
  ],
);

// ── Tema ──────────────────────────────────────────────────────────────────────
ThemeData _buildTheme() => ThemeData(
  useMaterial3: true,
  colorScheme: ColorScheme.fromSeed(seedColor: AppColors.teal600),
  scaffoldBackgroundColor: Colors.transparent,
  appBarTheme: const AppBarTheme(
    backgroundColor: Colors.transparent,
    elevation: 0,
    systemOverlayStyle: SystemUiOverlayStyle.light,
  ),
  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      backgroundColor: AppColors.teal900,
      foregroundColor: Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      textStyle: const TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5, fontSize: 12),
      minimumSize: const Size(double.infinity, 52),
    ),
  ),
  inputDecorationTheme: InputDecorationTheme(
    filled: true,
    fillColor: AppColors.teal50,
    border: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: BorderSide.none),
    enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: BorderSide.none),
    focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: const BorderSide(color: AppColors.teal600, width: 2)),
    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
  ),
  // Diálogos con esquinas más redondeadas
  dialogTheme: DialogTheme(
    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(28)),
  ),
  // Botones de texto
  textButtonTheme: TextButtonThemeData(
    style: TextButton.styleFrom(foregroundColor: AppColors.teal600, textStyle: const TextStyle(fontWeight: FontWeight.w700)),
  ),
);

// ── App ───────────────────────────────────────────────────────────────────────
class JourneyMateApp extends StatelessWidget {
  const JourneyMateApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'JourneyMate',
      debugShowCheckedModeBanner: false,
      theme: _buildTheme(),
      routerConfig: _router,
      builder: (context, child) => child ?? const SizedBox.shrink(),
    );
  }
}