"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";

interface UserProfile {
    name: string;
    surname: string;
    profilePicture: string;
}

export default function NavbarClient() {
    const [profile, setProfile] = useState<UserProfile | null>(null);
    const router = useRouter();

    useEffect(() => {
        const token = localStorage.getItem("jwt");
        if (!token) return;

        fetch("http://localhost:8090/profile", {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then((res) => res.ok ? res.json() : Promise.reject("Errore"))
            .then(setProfile)
            .catch(() => localStorage.removeItem("jwt"));
    }, []);

    return (
        <div className="flex justify-between items-center px-6 py-2">
            <Link href="/" className="flex items-center gap-3">
                <Image src="/logo.svg" alt="Logo" width={243} height={102} />
                <span className="sr-only">Homepage</span>
            </Link>

            {/* Profilo o Login a destra */}
            <div className="flex items-center gap-3">
                {profile ? (
                    <Link href="/userpage" className="flex items-center gap-2">
                        <span className="hidden sm:inline font-semibold text-xl">
                            {profile.name} {profile.surname}
                        </span>
                        <Avatar className="w-12 h-12">
                            <AvatarImage
                                src={
                                    profile.profilePicture
                                        ? `data:image/jpeg;base64,${profile.profilePicture}`
                                        : "/default-avatar.svg"
                                }
                            />
                            <AvatarFallback>
                                {profile.name[0]}{profile.surname[0]}
                            </AvatarFallback>
                        </Avatar>
                    </Link>
                ) : (
                    <Button
                        onClick={() => router.push("/login")}
                        className="bg-red-600 text-white"
                    >
                        Accedi subito!
                    </Button>
                )}
            </div>
        </div>
    );
}
