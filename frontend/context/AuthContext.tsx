"use client";

import { createContext, useContext, useEffect, useState } from "react";

interface UserProfile {
    name: string;
    surname: string;
    profilePicture: string;
}

interface AuthContextType {
    user: UserProfile | null;
    refetchUser: () => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<UserProfile | null>(null);

    const refetchUser = () => {
        const token = localStorage.getItem("jwt");
        if (!token) return setUser(null);

        fetch("http://localhost:8090/user/my-profile", {
            headers: { Authorization: `Bearer ${token}` },
        })
            .then((res) => (res.ok ? res.json() : Promise.reject("Errore")))
            .then(setUser)
            .catch(() => {
                localStorage.removeItem("jwt");
                setUser(null);
            });
    };

    const logout = () => {
        localStorage.removeItem("jwt");
        setUser(null);
    };

    useEffect(() => {
        refetchUser();
    }, []);

    return (
        <AuthContext.Provider value={{ user, refetchUser, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within AuthProvider");
    return context;
};
