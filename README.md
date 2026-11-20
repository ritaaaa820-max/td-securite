Djedid Aouatef

___________________________________________________
Partie 1

1. Que renvoie l’API après l’ajout de spring-boot-starter-security ?
L’API renvoie une erreur 401 (non autorisé) pour toutes les requêtes. Dès qu’on ajoute Spring Security, tout est bloqué si on n’est pas connecté.

5. Est-ce du chiffrement ?
Non. Quand on décode YWxpY2U6YWxpY2UxMjM=, on obtient alice:alice123. C’est seulement du Base64, et tout le monde peut le décoder facilement. Pour que le mot de passe soit vraiment protégé, il faudrait utiliser HTTPS.

7. Pourquoi y a-t-il une erreur CORS ?
Avant d’envoyer la vraie requête, le navigateur envoie une requête OPTIONS sans mot de passe. Spring Security la refusait (401), donc le front affichait une erreur CORS. Pour corriger, on autorise les requêtes OPTIONS et on ajoute http.cors(...) dans la configuration.

Piège : que faire du CSRF ?
On le désactive. Une attaque CSRF utilise les cookies que le navigateur envoie automatiquement. Dans notre cas, il n’y a pas de cookie ni de session (STATELESS) : le mot de passe est envoyé dans l’en-tête Authorization par notre code. Un autre site ne peut donc pas faire une requête à notre place.

Partie 2

4. Que se passe-t-il avec un mauvais mot de passe, puis si on recharge la page une fois connecté ?
Avec un mauvais mot de passe, le serveur répond 401 et un message d’erreur s’affiche.
Si on recharge la page, on est déconnecté et on revient sur la page de login. C’est parce que les identifiants sont gardés dans une variable, et une variable est effacée quand on recharge la page. On ne les enregistre pas dans le localStorage, parce qu’un script malveillant pourrait les lire.

Partie 3

5. Si on remet le bouton « Valider » à la main, qu’est-ce qui nous protège ?
C’est le serveur qui nous protège. Même si alice fait réapparaître le bouton avec F12, le serveur vérifie qu’elle est conseillère, et comme ce n’est pas le cas il répond 403. Le front sert seulement à l’affichage : la vraie sécurité doit toujours être faite côté serveur
