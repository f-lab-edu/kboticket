-- 경기정보 조회 
SELECT G.GAME_ID,
	   G.GAME_DD,
	   G.STADIUM_CD,
	   G.HOME_TEAM_CD,
	   G.AWAY_TEAM_CD,
	   G.START_TIME,
	   G.GAME_INFO,
	   S.STADIUM_NM
  FROM GAME G
  JOIN STADIUM S
    ON G.STADIUM_CD = S.STADIUM_CD;

-- 경기_정보_등록 
INSERT INTO GAME (GAME_ID, GAME_DD, STADIUM_CD, HOME_TEAM_CD, AWAY_TEAM_CD, START_TIME, GAME_INFO)
VALUES ('DA2404051830', '2024-04-05', 'DJ', 'HANHWA', 'KT', '18:30', '-');
INSERT INTO GAME (GAME_ID, GAME_DD, STADIUM_CD, HOME_TEAM_CD, AWAY_TEAM_CD, START_TIME, GAME_INFO)
VALUES ('DA2404051400', '2024-04-05', 'DJ', 'HANHWA', 'DOOSAN', '12:00', '-')

-- 경기정보 삭제 
DELETE FROM GAME 
 WHERE GAME_ID = '';
 
  
-- 경기를 해당 날짜로 등록/변경할 수 있는지 확인 
SELECT COUNT(*) AS CNT
  FROM GAME 
 WHERE 1=1
   AND STADIUM_CD = 'DJ'
   AND START_TIME != '18:30'
   AND GAME_DD = '2024-04-05';
   
-- 경기 정보 변경 
UPDATE GAME
   SET START_TIME = '18:30',
       GAME_INFO = '-'
 WHERE 1=1
   AND STADIUM_CD = 'DA2404051400';
  
  
  
  
-- 구단 정보 insert
INSERT INTO KBO_TEAM(TEAM_CD, TEAM_NM, 'HOME_CD')
VALUES ('HANHWA', '한화 이글스', 'DJ'),
       ('KIA', 'KIA 타이거즈', 'KW'),
       ('NC', 'NC 다이노스', 'CH'),
       ('KIWOOM', '키움 히어로즈', 'GO'),
       ('SSG', 'SSG 랜더스', 'IN'),
       ('SAMSUNG', '삼성 라이온즈', 'DG'),
       ('LG', 'LG 트윈스', 'SE'),
       ('KT', 'KT wiz', 'SU'),
       ('LOTTE', '롯데 자이언츠', 'BU'),
       ('DUSAN', '두산 베어스', 'SE')


-- 구장 정보 insert
INSERT INTO STADIUM (STADIUM_CD, STADIUM_NM, LOCATION)
VALUES ('DJ', '대전 한화생명 이글스파크', '대전광역시 중구 대종로 373'),
       ('KW', '기아 챔피언스 필드', '광주광역시 북구 서림로 10'),
       ('CH', '창원 NC 파크', '경상남도 창원시 마산회원구 삼호로 63 (양덕동)'),
       ('GO', '고척 스카이돔', '서울특별시 구로구 경인로 430'),
       ('IN', '인천 SSG 랜더스필드', '인천광역시 미추홀구 문학동 인천문학경기장'),
       ('DG', '대구 삼성 라이온즈 파크', '대구광역시 수성구 야구전설로 1 대구삼성라이온즈파크'),
       ('SE', '서울종합운동장 야구장', '서울 송파구 올림픽로 25 서울종합운동장 잠실야구장'),
       ('SU', '수원 케이티 위즈 파크', '경기도 수원시 장안구 경수대로 893'),
       ('BU', '부산 사직 야구장', '부산광역시 동래구 사직로 45')


-- 좌석 insert
INSERT INTO SEATS (SEAT_NO, STADIUM_CD, SEAT_LEVEL, SEAT_BLOCK, ASSIGN_YN)
VALUES ('C8', 'DJ', 'IN1', '101', 'N')
  
  

  