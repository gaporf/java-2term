concat([], B, B).
concat([H | T], B, [H | R]) :- concat(T, B, R).
first([], []).
first([(KEY, VALUE)|REST], R) :- first(REST, A), concat([[KEY, VALUE]], A, R).
second([], []).
second([[KEY, VALUE]], [[KEY], [VALUE]]).
second([[KEY1, VALUE1], [KEY2, VALUE2]], [[KEY1], [[KEY1], [VALUE1]], [[KEY2], [VALUE2]]]).
second([[KEY1],LEFT, [[KEYR], VALUE], [KEY2], [VALUE2]], [[KEYR], [[KEY1], LEFT, [[KEYR], VALUE]], [[KEY2], [VALUE2]]]).
second([[KEY1],LEFT1, [[KEYR1], VALUE1], [KEY2], LEFT2, RIGHT2], [[KEYR1], [[KEY1], LEFT1, [[KEYR1], VALUE1]], [[KEY2], LEFT2, RIGHT2]]).
second([[KEY1, VALUE1], [KEY2, VALUE2], [KEY3, VALUE3]|REST], R) :- second([[KEY1, VALUE1], [KEY2, VALUE2]], A), concat([[KEY3, VALUE3]], REST, B), second(B, C), concat(A, C, D), second(D, R).
tree_build(V, R) :- first(V, A), second(A, R).

map_get([[KEY], [VALUE]], KEY, VALUE).

map_get([[KEY1], LEFT, RIGHT], KEY, VALUE) :-
	KEY =< KEY1,
	map_get(LEFT, KEY, VALUE).

map_get([[KEY1], LEFT, RIGHT], KEY, VALUE) :-
	KEY > KEY1,
	map_get(RIGHT, KEY, VALUE).

map_floorKey([[KEY1], [VALUE]], KEY, FloorKey) :-
	KEY1 =< KEY,
	FloorKey is KEY!.

map_floorKey([[KEY1], LEFT, RIGHT], KEY, FloorKey) :-
	KEY =< KEY1,
	map_floorKey(LEFT, KEY, VALUE).

map_floorKey([[KEY1], LEFT, RIGHT], KEY, FloorKey) :-
	KEY > KEY1,
	map_floorKey(RIGHT, KEY, VALUE).
