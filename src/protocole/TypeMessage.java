package protocole;


public enum TypeMessage {
    DIFFUSION_CONNECTION,
    DIFFUSION_DEBUT_JEU,
    REPONSE_CONNECTION,
    DIFFUSION_NUMEROTATION,//Lance l'etape de numerotation
    MESSAGE_NUMEROTATION,//les joueurs envoient leur numero
    REPONSE_NUMEROTATION,//les joueurs se renvoient un ack 
    DIFFUSION_FIN_NUMEROTATION,
    DIFFUSION_EJECTION,
    MESSAGE_ELECTION,
    DIFFUSION_MAITRE
    
}
