export const CLIENT_ERROR_CODE = Object.freeze({
  HTTP_ERROR: "HTTP_ERROR",
  NETWORK_ERROR: "NETWORK_ERROR",
  REQUEST_TIMEOUT: "REQUEST_TIMEOUT",
  UNKNOWN_ERROR: "UNKNOWN_ERROR",
});

const CLIENT_ERROR_MESSAGE = Object.freeze({
  [CLIENT_ERROR_CODE.HTTP_ERROR]: "요청을 처리하지 못했습니다.",
  [CLIENT_ERROR_CODE.NETWORK_ERROR]:
    "서버에 연결할 수 없습니다. 네트워크 상태를 확인해 주세요.",
  [CLIENT_ERROR_CODE.REQUEST_TIMEOUT]:
    "요청 시간이 초과되었습니다. 잠시 후 다시 시도해 주세요.",
  [CLIENT_ERROR_CODE.UNKNOWN_ERROR]: "알 수 없는 오류가 발생했습니다.",
});

export class ApiError extends Error {
  constructor({
    status = null,
    code = CLIENT_ERROR_CODE.UNKNOWN_ERROR,
    message = CLIENT_ERROR_MESSAGE[CLIENT_ERROR_CODE.UNKNOWN_ERROR],
    fieldErrors = [],
    timestamp = null,
    path = null,
    cause = null,
  } = {}) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
    this.fieldErrors = fieldErrors;
    this.timestamp = timestamp;
    this.path = path;
    this.cause = cause;
  }
}

export function normalizeApiError(error) {
  if (error instanceof ApiError) return error;

  if (error?.code === "ECONNABORTED" || error?.code === "ETIMEDOUT") {
    return new ApiError({
      code: CLIENT_ERROR_CODE.REQUEST_TIMEOUT,
      message: CLIENT_ERROR_MESSAGE[CLIENT_ERROR_CODE.REQUEST_TIMEOUT],
      cause: error,
    });
  }

  if (error?.response) {
    const responseBody = error.response.data;
    return new ApiError({
      status: error.response.status,
      code: responseBody?.code || CLIENT_ERROR_CODE.HTTP_ERROR,
      message:
        responseBody?.message ||
        CLIENT_ERROR_MESSAGE[CLIENT_ERROR_CODE.HTTP_ERROR],
      fieldErrors: Array.isArray(responseBody?.fieldErrors)
        ? responseBody.fieldErrors
        : [],
      timestamp: responseBody?.timestamp || null,
      path: responseBody?.path || null,
      cause: error,
    });
  }

  if (error?.request) {
    return new ApiError({
      code: CLIENT_ERROR_CODE.NETWORK_ERROR,
      message: CLIENT_ERROR_MESSAGE[CLIENT_ERROR_CODE.NETWORK_ERROR],
      cause: error,
    });
  }

  return new ApiError({
    message:
      error?.message || CLIENT_ERROR_MESSAGE[CLIENT_ERROR_CODE.UNKNOWN_ERROR],
    cause: error,
  });
}
