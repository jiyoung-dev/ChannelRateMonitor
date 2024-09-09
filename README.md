# 채널 점유율 모니터링 

## 0. 설명   
DB로 15초마다 채널점유율 테이블을 모니터링한다. 
임계치 초과시, 최초 1회만 로그작성 
임계치 이하시, 해제되었다는 로그 작성 

## 1. 실행방법 
./run.bat 실행 

## 2. 빌드방법 
./build.bat 실행 

## 3. 사용가이드 
- OAMP Web에서 코드명으로 임계치 숫자를 변경할수있다. 
- 로그파일은 매일 자정마다 초기화된다. 