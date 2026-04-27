import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';

// ─── Pages (crea estos archivos en lib/pages/) ───────────────────────────────
// import 'pages/home_page.dart';
// import 'pages/search_page.dart';
// import 'pages/login_page.dart';
// import 'pages/register_page.dart';
// import 'pages/my_bookings_page.dart';
// import 'pages/payment_success_page.dart';
// import 'pages/payment_cancelled_page.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Orientación bloqueada a portrait (quita si quieres landscape)
  await SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);

  // Barra de estado transparente (efecto edge-to-edge)
  SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
    statusBarColor: Colors.transparent,
    statusBarIconBrightness: Brightness.dark,
  ));

  runApp(const JourneyMateApp());
}

// ─── Colores de la app (equivalente a los teal-* de Tailwind) ─────────────────
class AppColors {
  static const teal900 = Color(0xFF0F3D3A);
  static const teal800 = Color(0xFF134E4A);
  static const teal700 = Color(0xFF0F766E);
  static const teal600 = Color(0xFF0D9488);
  static const teal500 = Color(0xFF14B8A6);
  static const teal400 = Color(0xFF2DD4BF);
  static const teal300 = Color(0xFF5EEAD4);
  static const teal200 = Color(0xFF99F6E4);
  static const lime    = Color(0xFFE9FC9E);   // color acento del gradiente
  static const white70 = Color(0xB3FFFFFF);
  static const white40 = Color(0x66FFFFFF);
}

// ─── Tema global ──────────────────────────────────────────────────────────────
ThemeData _buildTheme() {
  return ThemeData(
    useMaterial3: true,
    colorScheme: ColorScheme.fromSeed(
      seedColor: AppColors.teal600,
      brightness: Brightness.light,
    ),
    fontFamily: 'Geist', // añade en pubspec.yaml o usa GoogleFonts.geist()
    scaffoldBackgroundColor: Colors.transparent,
    appBarTheme: const AppBarTheme(
      backgroundColor: Colors.transparent,
      elevation: 0,
      systemOverlayStyle: SystemUiOverlayStyle.dark,
      titleTextStyle: TextStyle(
        color: AppColors.teal900,
        fontWeight: FontWeight.w900,
        fontSize: 18,
        letterSpacing: -0.5,
      ),
    ),
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        backgroundColor: AppColors.teal900,
        foregroundColor: Colors.white,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        textStyle: const TextStyle(
          fontWeight: FontWeight.w900,
          letterSpacing: 2,
          fontSize: 11,
        ),
        minimumSize: const Size(double.infinity, 56),
      ),
    ),
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: AppColors.white40,
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(20),
        borderSide: BorderSide(color: Colors.white.withOpacity(0.6)),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(20),
        borderSide: BorderSide(color: Colors.white.withOpacity(0.6)),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(20),
        borderSide: const BorderSide(color: AppColors.teal600, width: 2),
      ),
      labelStyle: const TextStyle(color: AppColors.teal700, fontWeight: FontWeight.w600),
      hintStyle: TextStyle(color: AppColors.teal700.withOpacity(0.5)),
      contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
    ),
    cardTheme: CardThemeData(
      color: AppColors.white70,
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(24),
        side: BorderSide(color: Colors.white.withOpacity(0.6)),
      ),
    ),
  );
}

// ─── Router (go_router — equivalente a react-router) ─────────────────────────
final GoRouter _router = GoRouter(
  initialLocation: '/',
  routes: [
    GoRoute(
      path: '/',
      // builder: (context, state) => const HomePage(),
      builder: (context, state) => const _PlaceholderPage(title: 'Home'),
    ),
    GoRoute(
      path: '/buscar',
      // builder: (context, state) => const SearchPage(),
      builder: (context, state) => const _PlaceholderPage(title: 'Buscador'),
    ),
    GoRoute(
      path: '/login',
      // builder: (context, state) => const LoginPage(),
      builder: (context, state) => const _PlaceholderPage(title: 'Login'),
    ),
    GoRoute(
      path: '/register',
      // builder: (context, state) => const RegisterPage(),
      builder: (context, state) => const _PlaceholderPage(title: 'Registro'),
    ),
    GoRoute(
      path: '/mis-reservas',
      // builder: (context, state) => const MyBookingsPage(),
      builder: (context, state) => const _PlaceholderPage(title: 'Mis Reservas'),
    ),
    GoRoute(
      path: '/pago-exitoso',
      // builder: (context, state) => const PaymentSuccessPage(),
      builder: (context, state) => const _PlaceholderPage(title: 'Pago Exitoso'),
    ),
    GoRoute(
      path: '/pago-cancelado',
      // builder: (context, state) => const PaymentCancelledPage(),
      builder: (context, state) => const _PlaceholderPage(title: 'Pago Cancelado'),
    ),
  ],
);

// ─── App root ─────────────────────────────────────────────────────────────────
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
        // Envuelve toda la app en el gradiente de fondo
        return Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
              stops: [0.0, 0.5, 1.0],
              colors: [
                Color(0xFF1CB5B0), // teal
                AppColors.lime,    // lima
                Color(0xFF1CB5B0), // teal
              ],
            ),
          ),
          child: child ?? const SizedBox.shrink(),
        );
      },
    );
  }
}

// ─── Placeholder temporal (borra cuando tengas las pages reales) ──────────────
class _PlaceholderPage extends StatelessWidget {
  final String title;
  const _PlaceholderPage({required this.title});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.travel_explore, size: 80, color: AppColors.teal900),
            const SizedBox(height: 16),
            Text(
              'JourneyMate',
              style: Theme.of(context).textTheme.headlineLarge?.copyWith(
                fontWeight: FontWeight.w900,
                color: AppColors.teal900,
                letterSpacing: -1,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              title.toUpperCase(),
              style: const TextStyle(
                fontSize: 10,
                fontWeight: FontWeight.w900,
                letterSpacing: 6,
                color: AppColors.teal700,
              ),
            ),
            const SizedBox(height: 32),
            TextButton(
              onPressed: () => context.go('/buscar'),
              child: const Text('Ir al Buscador →'),
            ),
          ],
        ),
      ),
    );
  }
}