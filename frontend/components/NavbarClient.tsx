"use client";

import { useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/context/AuthContext";

export default function NavbarClient() {
    const { user } = useAuth(); // 👈 Usa context
    const router = useRouter();

    return (
        <div className="flex justify-between items-center px-6 py-2">
            <Link href="/" className="flex items-center gap-3">
                <Image src="/logo.svg" alt="Logo" width={243} height={102} />
                <span className="sr-only">Homepage</span>
            </Link>

            <div className="flex items-center gap-3">
                {user ? (
                    <Link href="/userpage" className="flex items-center gap-2">
                        <span className="hidden sm:inline font-semibold text-xl">
                            {user.name} {user.surname}
                        </span>
                        <Avatar className="w-12 h-12">
                            <AvatarImage
                                src={
                                    user.profilePicture
                                        ? `data:image/jpeg;base64,${user.profilePicture}`
                                        : "/default-avatar.svg"
                                }
                            />
                            <AvatarFallback>
                                {user.name[0]}{user.surname[0]}
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
