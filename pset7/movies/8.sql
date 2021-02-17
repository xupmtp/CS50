select p.name from stars as s
join people as p
on s.person_id = p.id
where movie_id in (select id from movies where title = "Toy Story");