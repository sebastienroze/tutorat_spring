
INSERT INTO `utilisateur` (`id`, `mot_de_passe`, `pseudo`) VALUES
(1, '$2a$10$uz5dB8kKtjb37GwBLXUtEeALDOq4Ge/DHx2CmXWOf.hd1ave7Al0i', 'a'),
(2, '$2a$10$uz5dB8kKtjb37GwBLXUtEeALDOq4Ge/DHx2CmXWOf.hd1ave7Al0i', 'b'),
(3, '$2a$10$uz5dB8kKtjb37GwBLXUtEeALDOq4Ge/DHx2CmXWOf.hd1ave7Al0i', 'c'),
(4, '$2a$10$uz5dB8kKtjb37GwBLXUtEeALDOq4Ge/DHx2CmXWOf.hd1ave7Al0i', 'michel');

INSERT INTO `role` (`id`, `denomination`) VALUES
(1, 'ROLE_UTILISATEUR'),
(2, 'ROLE_ADMINISTRATEUR');

INSERT INTO `utilisateur_role` (`utilisateur_id`, `role_id`) VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 1),
(4, 1);



INSERT INTO `niveau` (`id`, `denomination`) VALUES
(6, 'Sixième'),
(5, 'Cinquième'),
(4, 'Quatrième'),
(3, 'Troisième'),
(2, 'Seconde'),
(1, 'Terminale');



INSERT INTO `reponse` (`id`, `date_reponse`,`oral`,`photo`)  VALUES
(1,'2021-06-25','reponse.3gp','reponse.png');


INSERT INTO `question` (`id`, `sujet`,`explication`, `utilisateur_id`,`niveau_id`,`date_question`,`oral`,`photo`,`reponse_id`)  VALUES
(1, 'calcul : périmetre','comment faire ?', 3,2,'2021-06-24','audio.3gp','photo.jpg',1),
(2, 'gemoétrie : milieu','ou est il ?',3,3,'2021-06-24',NULL,NULL,NULL),
(3, 'calcul : aire','au carré ?', 2,4,'2021-06-24',NULL,NULL,NULL),
(4, 'gemoétrie : quadrature','impossible ?', 2,5,'2021-06-24',NULL,NULL,NULL);




