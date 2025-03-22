# ranking

### API
- GET /v1/ranking/user/{userId}
- GET /v1/ranking/leaderboard
- GET /v1/ranking/leaderboard/top/{count}
- POST /v1/ranking/user/{userId}/increase/{score}
- POST /v1/ranking/user/{userId}/decrease/{score}

참고 글
- [Redis와 클라이언트 캐시 간 데이터 동기화 기술 - Redis Client Caching 살펴보기](https://devocean.sk.com/blog/techBoardDetail.do?ID=167301&boardType=techBlog)
- [Redis를 사용하여 랭킹 시스템을 구현하면 정말 빠를까?](https://velog.io/@koomin1227/Redis%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-%EB%9E%AD%ED%82%B9-%EC%8B%9C%EC%8A%A4%ED%85%9C%EC%9D%84-%EA%B5%AC%ED%98%84%ED%95%98%EB%A9%B4-%EC%A0%95%EB%A7%90-%EB%B9%A0%EB%A5%BC%EA%B9%8C)
- [Leaderboard System Design](https://systemdesign.one/leaderboard-system-design/)