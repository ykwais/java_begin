# Семестр 1. Лабораторная работа №5.

Найти GAP в логах приложения. Реализуйте программу, которая ищет пики отклонения по скорости выполнения запросов на
основе логов приложения. Лог приложения имеет дату и время начала запроса, типовой текст начала выполнения запроса или
окончание выполнения запроса. Программа должна выполнить анализ файла с логами, выявить среднее время выполнения запроса
и выдать запросы, которые имеют отклонение от медианного значения на заданную величину.

Запросы в логах могут быть асинхронные, пример:

> a. 2019-09-01 00:05:00 – INFO – QUERY FOR ID = 1
>
> b. 2019-09-01 00:05:01 – INFO – QUERY FOR ID = 2
>
> c. 2019-09-01 00:05:03 – INFO – RESULT QUERY FOR ID = 1
>
> d. 2019-09-01 00:05:10 – INFO – QUERY FOR ID = 4
>
> e. 2019-09-01 00:05:12 – INFO – QUERY FOR ID = 3
>
> f. 2019-09-01 00:05:13 – INFO – RESULT QUERY FOR ID = 3
>
> g. 2019-09-01 00:05:13 – INFO – RESULT QUERY FOR ID = 4
>
> h. 2019-09-01 00:05:30 – INFO – RESULT QUERY FOR ID = 2
>

Самый аномальный запрос – QUERY FOR ID 2, который выполнялся на порядок дольше, чем остальные запросы.

На вход приложению подается файл с логами (от нескольких байт, до десятков гигабайт) и параметр отклонения, например, 3
sec или 3 min. Если параметр не задан, то программа самостоятельно должна выявить аномальные запросы (разработчик сам
определяет алгоритм). Необходимо отображать прогресс работы (сколько обработано логов, сколько осталось
обработать логов). Также, в связи с возможным большим размером файла, необходимо использовать средства многопоточной
обработки

Пользовательский интерфейс для программы необязателен.

Процесс сдачи лабораторной работы:
1. Клонируете репозиторий
![github-1](https://github.com/new94/JavaServiceBrackets/assets/3996014/79ae3da4-cfc6-4fe1-ae8f-36cea470993b)
2. Переходите в ветку develop (checkout)
3. На основе ветки develop создаёте свою ветку с названием по шаблону student/номергруппы_фамилия_перваябукваимени
4. Пишите код в своей ветке student/номергруппы_фамилия_перваябукваимени
5. Проверяйте код тестами
![github-2](https://github.com/new94/JavaServiceBrackets/assets/3996014/7eb73962-ef01-4e0a-bcb0-a05dd1406d01)
6. Если все тесты пройдены, то можно отправлять код на проверку, для этого нужно создать Pull Request
7. В репозитории в github перейдите во вкладку Pull Requests
8. Выберите в base ветку develop, а в compare свою ветку, например (student/0000_nenakhov_e)
![github-3](https://github.com/new94/JavaServiceBrackets/assets/3996014/eb7c329c-1581-4a0f-ab5b-79ff3061e6d4)
9. Нажимаете create pull request
10. Далее выбираете в Reviewers справа new94 (Ненахов Евгений)
11. Далее выбираете в Assignees справа new94 и себя
12. В описании Pull Request пишите "Фамилия Имя - лабороторная работа", например "Ненахов Евгений - лабораторная работа"
![image](https://github.com/new94/JavaServiceBrackets/assets/3996014/ed1553d6-1d41-41f2-844a-c24a3f69ca85)
13. Нажимаете create pull request
14. После создания pull request запускается автоматическая проверка тестов. Все тесты должны быть пройдены. Если тесты не пройдены, лабораторная работа проверяться не будет. Чтобы перезапустить автоматическую проверку, нужно переоткрыть pull request. 
15. Ненахов Евгений смотрит код и оставляет комментарии к коду. Все комментарии нужно либо поправить, либо ответить, но закрывать pull request нельзя!
16. Если лабораторная работа не сдана, то будет комментарий от Ненахов Евгений о том, что нужно поправить, чтобы сдать
17. Если с кодом всё хорошо, то будет комментарий, что лабораторная работа сдана.
18. Делать commit и push в любые ветки, кроме своей строго запрещено!
