export const categoryTypes = [
  "전체",
  "연락",
  "데이트",
  "돈",
  "이성친구",
  "기타",
];

export const relationshipTypes = ["전체 관계", "연인", "썸", "부부"];

export const liveTrials = [
  {
    id: 101,
    title: "10년 사귀고 헤어진지 3개월 만에 결혼 소식",
    viewerCount: "1,240",
  },
  {
    id: 102,
    title: "데이트 비용 9:1로 내는 남친, 경제적 차이 때문이라는데 정당한가요?",
    viewerCount: "866",
  },
  {
    id: 103,
    title: "여사친과 단둘이 1박 2일 여행을 가는 남친, 이해해줘야 하나요?",
    viewerCount: "2,703",
  },
];

export const communityPosts = [
  {
    id: 1,
    category: "연락",
    relationshipType: "썸",
    title: "매일 밤 11시에만 연락오는 썸남, 이거 어장인가요?",
    summary:
      "낮에는 카톡 답도 없다가 밤만 되면 연락해요. 진지한 관계로 생각해도 될까요?",
    createdAt: "10분 전",
    likeCount: 124,
    commentCount: 42,
    isLive: true,
  },
  {
    id: 2,
    category: "데이트",
    relationshipType: "연인",
    title: "기념일 데이트 코스 평가 부탁드립니다",
    summary:
      "오랜만에 둘만의 시간을 보내려고 합니다. 무리하지 않으면서 기억에 남을 코스일까요?",
    createdAt: "56분 전",
    likeCount: 89,
    commentCount: 15,
    isLive: false,
  },
  {
    id: 3,
    category: "돈",
    relationshipType: "연인",
    title: "데이트 통장, 누가 더 많이 내야 할까요?",
    summary:
      "서로의 수입 차이가 있을 때 공평한 기준이 무엇인지 다른 분들의 의견이 궁금합니다.",
    createdAt: "2시간 전",
    likeCount: 206,
    commentCount: 108,
    isLive: true,
  },
  {
    id: 4,
    category: "이성친구",
    relationshipType: "연인",
    title: "내 친구의 여우짓을 남자친구가 거절을 안해",
    summary:
      "며칠 전 나, 남자친구, 내 친구와 함께 술자리를 했는데 친구가 '오빠가 아깝다~'라며 자꾸 배배꼬고 선을 넘었어요. 남자친구는 그 자리에서 허허 웃기만 했고, 제가 '쟤 자꾸 왜 저래'라고 하자 '에이 그냥 한 소리지'라며 넘기더라고요. 남자친구가 확실히 선을 못 긋는 것 같아서 불편합니다. 나만 이렇게 생각하나요?",
    createdAt: "5시간 전",
    likeCount: 80,
    commentCount: 23,
    isLive: false,
  },
  {
    id: 5,
    category: "기타",
    relationshipType: "부부",
    title: "결혼 준비 중인데 배우자와 자꾸 다퉈요",
    summary: "준비 과정에서 서로의 기준이 너무 달라 고민입니다.",
    createdAt: "어제",
    likeCount: 102,
    commentCount: 49,
    isLive: false,
  },
];
