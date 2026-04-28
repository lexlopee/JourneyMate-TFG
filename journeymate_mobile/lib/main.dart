import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';
import 'package:lucide_icons/lucide_icons.dart';
import 'package:intl/date_symbol_data_local.dart';

import 'core/app_colors.dart';
import 'services/auth_service.dart';

// Screens
import 'screens/home_screen.dart';
import 'screens/search_screen.dart';
import 'screens/login_screen.dart';
import 'screens/register_screen.dart';
import 'screens/payment/payment_screens.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Inicializar locale español (para intl / fechas)
  await initializeDateFormatting('es_ES', null);

  // Orientación portrait
  await SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);

  // Barra de estado transparente
  SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
    statusBarColor: Colors.transparent,
    statusBarIconBrightness: Brightness.dark,
  ));

  // Restaurar sesión guardada (token en SharedPreferences)
  await AuthService.restoreSession();

  runApp(const JourneyMateApp());
}

// ── Router ────────────────────────────────────────────────────────────────────
final GoRouter _router = GoRouter(
  initialLocation: '/',
  routes: [
    GoRoute(
      path: '/',
      builder: (_, __) => const HomeScreen(),
    ),
    GoRoute(
      path: '/buscar',
      builder: (_, state) => SearchScreen(
        initialTab:      state.uri.queryParameters['tab'],
        initialToId:     state.uri.queryParameters['toId'],
        initialDestText: state.uri.queryParameters['destText'],
      ),
    ),
    GoRoute(
      path: '/login',
      builder: (_, __) => const LoginScreen(),
    ),
    GoRoute(
      path: '/register',
      builder: (_, __) => const RegisterScreen(),
    ),
    GoRoute(
      path: '/pago-exitoso',
      builder: (_, state) => PaymentSuccessScreen(
        reservaId: state.uri.queryParameters['reservaId'],
        metodo:    state.uri.queryParameters['metodo'],
      ),
    ),
    GoRoute(
      path: '/pago-cancelado',
      builder: (_, __) => const PaymentCancelledScreen(),
    ),
    GoRoute(
      path: '/mis-reservas',
      builder: (_, __) => const _MyBookingsPlaceholder(),
    ),
  ],
);

// ── Tema ──────────────────────────────────────────────────────────────────────
ThemeData _buildTheme() => ThemeData(
  useMaterial3: true,
  colorScheme: ColorScheme.fromSeed(seedColor: AppColors.teal600, brightness: Brightness.light),
  scaffoldBackgroundColor: Colors.transparent,
  appBarTheme: const AppBarTheme(
    backgroundColor: Colors.transparent, elevation: 0,
    systemOverlayStyle: SystemUiOverlayStyle.dark,
  ),
  elevatedButtonTheme: ElevatedButtonThemeData(
    style: ElevatedButton.styleFrom(
      backgroundColor: AppColors.teal900, foregroundColor: Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      textStyle: const TextStyle(fontWeight: FontWeight.w900, letterSpacing: 2, fontSize: 11),
      minimumSize: const Size(double.infinity, 52),
    ),
  ),
  inputDecorationTheme: InputDecorationTheme(
    filled: true, fillColor: AppColors.white40,
    border: OutlineInputBorder(borderRadius: BorderRadius.circular(20), borderSide: BorderSide.none),
    enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(20), borderSide: BorderSide.none),
    focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(20), borderSide: const BorderSide(color: AppColors.teal600, width: 2)),
  ),
);

// ── App root ──────────────────────────────────────────────────────────────────
class JourneyMateApp extends StatelessWidget {
  const JourneyMateApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'JourneyMate',
      debugShowCheckedModeBanner: false,
      theme: _buildTheme(),
      routerConfig: _router,
      builder: (context, child) {
        // Envuelve toda la app en el gradiente de fondo global
        return Container(
          decoration: const BoxDecoration(gradient: AppColors.gradientMain),
          child: child ?? const SizedBox.shrink(),
        );
      },
    );
  }
}

// ── Placeholder Mis Reservas (sustituye cuando tengas la screen real) ─────────
class _MyBookingsPlaceholder extends StatelessWidget {
  const _MyBookingsPlaceholder();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Container(
        decoration: const BoxDecoration(gradient: AppColors.gradientMain),
        child: SafeArea(
          child: Column(children: [
            Padding(
              padding: const EdgeInsets.all(16),
              child: Row(children: [
                GestureDetector(onTap: () => context.go('/'), child: const Icon(LucideIcons.arrowLeft, color: AppColors.teal900)),
                const SizedBox(width: 12),
                const Text('MIS RESERVAS', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.teal900, letterSpacing: -0.3)),
              ]),
            ),
            Expanded(child: Center(child: Column(mainAxisSize: MainAxisSize.min, children: [
              const Icon(LucideIcons.bookOpen, size: 64, color: AppColors.teal400),
              const SizedBox(height: 16),
              const Text('Mis Reservas', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w900, color: AppColors.teal900)),
              const SizedBox(height: 8),
              const Text('Pantalla en construcción', style: TextStyle(color: AppColors.teal600)),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: () => context.go('/'),
                style: ElevatedButton.styleFrom(minimumSize: const Size(200, 48)),
                child: const Text('VOLVER AL INICIO'),
              ),
            ]))),
          ]),
        ),
      ),
    );
  }
}