package com.gabojait.gabojaitspring.profile.domain.type;

public enum Position {

    DESIGNER,
    BACKEND,
    FRONTEND,
    MANAGER,
    NONE;

    public static String toKorean(Position position) {
        String pInKorean = "";
        switch (position.name()) {
            case "DESIGNER":
                pInKorean = "디자이너";
                break;
            case "BACKEND":
                pInKorean = "백엔드 개발자";
                break;
            case "FRONTEND":
                pInKorean = "프런트엔드 개발자";
                break;
            case "MANAGER":
                pInKorean = "프로젝트 매니저";
                break;
        }

        return pInKorean;
    }
}
