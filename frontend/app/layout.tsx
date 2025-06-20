import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import {
    NavigationMenu,
    NavigationMenuItem,
    NavigationMenuLink,
    NavigationMenuList,
    navigationMenuTriggerStyle,
} from "@/components/ui/navigation-menu"
import Link from "next/link";
import {Separator} from "@/components/ui/separator";

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});

const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
});

export const metadata: Metadata = {
    title: "AdottaTO",
    description: "Generated by create next app",
};

export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="it">
        <body
            className={`${geistSans.variable} ${geistMono.variable} antialiased`}
        >
        <NavigationMenu className="my-4" >
            <NavigationMenuList>
                <NavigationMenuItem>
                    <NavigationMenuLink asChild className={navigationMenuTriggerStyle()}>
                        <Link href="/">HomePage</Link>
                    </NavigationMenuLink>
                    <NavigationMenuLink asChild className={navigationMenuTriggerStyle()}>
                        <Link href="/userpage">UserPage</Link>
                    </NavigationMenuLink>
                    <NavigationMenuLink asChild className={navigationMenuTriggerStyle()}>
                        <Link href="/login">Login</Link>
                    </NavigationMenuLink>
                </NavigationMenuItem>
            </NavigationMenuList>
        </NavigationMenu>

        <Separator  className="my-4" />

        {children}
        </body>
        </html>
    );
}
